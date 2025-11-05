
package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    
    private List<T> content; // La lista degli elementi della pagina corrente (es. PropertyDTO)
    private int currentPage;   // Il numero della pagina corrente
    private long totalElements; // Il numero totale di elementi in tutte le pagine
    private int totalPages;    // Il numero totale di pagine
    private boolean isLast;      // True se questa è l'ultima pagina
}