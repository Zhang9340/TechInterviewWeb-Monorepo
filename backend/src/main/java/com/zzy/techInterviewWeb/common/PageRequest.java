package com.zzy.techInterviewWeb.common;

import com.zzy.techInterviewWeb.constant.CommonConstant;
import lombok.Data;
/**
 * Pagination Request
 */
@Data
public class PageRequest {

    /**
     * Current page number
     */
    private int current = 1;

    /**
     * Page size
     */
    private int pageSize = 10;

    /**
     * Sort field
     */
    private String sortField;

    /**
     * Sort order (default is ascending)
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
