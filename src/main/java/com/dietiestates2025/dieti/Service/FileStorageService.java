package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.exception.FileStorageException;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;


@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    /**
     * Costruttore del FileStorageService che inizializza la directory di archiviazione dei file.
     * Se la directory specificata non esiste, viene creata automaticamente.
     *
     * @param uploadDir Il percorso della directory di upload, iniettato dalle proprietà dell'applicazione (es. application.properties).
     * @throws FileStorageException se non è stato possibile creare la directory di archiviazione.
     */
    public FileStorageService(@Value("${spring.servlet.multipart.location}") String uploadDir) {
        // Normalizza e rende assoluto il percorso della directory per garantirne la coerenza.
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            // Crea la directory (e le eventuali directory parent) se non esistono.
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    

    /**
     * Archivia un file caricato nella directory di destinazione.
     * Il metodo esegue la pulizia (sanitizzazione) del nome del file originale, genera un nome univoco tramite UUID
     * per prevenire conflitti e nascondere i nomi originali, e infine salva il file.
     *
     * @param file L'oggetto {@link MultipartFile} che rappresenta il file da archiviare.
     * @return Il nome del file univoco e generato casualmente con cui il file è stato salvato.
     * @throws FileStorageException se il nome del file non è valido o se si verifica un errore di I/O durante il salvataggio.
     */
    public String storeFile(MultipartFile file) {
        // Pulisce il nome del file da caratteri potenzialmente dannosi o sequenze di percorso (es. ../).
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Controlla che il nome del file non sia vuoto o contenga sequenze di percorso non valide.
        if (originalFileName.isEmpty() || originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains an invalid path sequence or is empty: " + originalFileName);
        }

        try {
            // Genera un nome file univoco per evitare sovrascritture e problemi di sicurezza.
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Risolve il percorso completo del file di destinazione.
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            
            // Copia il contenuto del file caricato nella destinazione finale, sovrascrivendo se esiste.
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * Carica un file come risorsa (`Resource`), eseguendo controlli di sicurezza per prevenire attacchi di tipo "path traversal".
     * Il metodo verifica che il percorso del file richiesto sia confinato all'interno della directory di archiviazione
     * prima di restituirlo.
     *
     * @param fileName Il nome del file da caricare.
     * @return Un oggetto {@link Resource} che rappresenta il file richiesto, pronto per essere inviato al client.
     * @throws ResourceNotFoundException se il file non viene trovato, non è leggibile, o se il percorso tenta
     *                                   di accedere a una directory esterna a quella di archiviazione.
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            // Risolve il percorso del file relativo alla directory di archiviazione.
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            // Verifica che il percorso risolto si trovi ancora all'interno della directory di archiviazione.
            // Questo impedisce di accedere a file in altre parti del sistema (es. /etc/passwd).
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new ResourceNotFoundException("Cannot access file outside of the designated storage directory: " + fileName);
            }

            // Crea una risorsa a partire dall'URI del file
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or is not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found (Malformed URL): " + fileName, ex);
        }
    }
}