package com.elearning.models.searchs;

import java.io.Serializable;
import java.util.List;

public class ParameterSearchCategory implements Serializable, Cloneable{

    private String id;

    private Integer level;

    private Boolean isDeleted;

    private List<String> categoriesIds;

    private List<String> parentIds;
}
