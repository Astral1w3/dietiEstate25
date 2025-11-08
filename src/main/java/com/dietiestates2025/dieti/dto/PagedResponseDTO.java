
package com.dietiestates2025.dieti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    
    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}