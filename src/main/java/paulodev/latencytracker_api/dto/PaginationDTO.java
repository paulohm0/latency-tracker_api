package paulodev.latencytracker_api.dto;

public record PaginationDTO(
        int totalPages,
        int currentPage,
        int pageSize
) { }
