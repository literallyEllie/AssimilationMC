package net.assimilationmc.assicore.util;

public class UtilPaging {

    public static int getPageCount(int elements, int maxPerPage) {
        return (int) Math.round(Math.ceil(elements / maxPerPage));
    }

    public static int getPageElementIndex(int currentPage, int totalPages, int maxElementsPerPage) {
        int startIndex = 0;
        if (currentPage >= 0 && currentPage < totalPages) {
            startIndex = currentPage * maxElementsPerPage;
        }
        return startIndex;
    }

    public static int getCountPerPage(int pageSize, int totalElements, int firstResults) {
        return Math.min(pageSize, totalElements - firstResults);
    }

}
