package com.nexigroup.pagopa.cruscotto.domain.enumeration;

public enum ModuleCode {
    A1("A.1"),
    A2("A.2"),
    B1("B.1"),
    B2("B.2"),
    B3("B.3"),
    B4("B.4"),
    B5("B.5"),
    B8("B.8"),
    B9("B.9");

    public final String code;

    ModuleCode(String code) {
        this.code = code;
    }

    public static ModuleCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ModuleCode moduleCode : ModuleCode.values()) {
            if (moduleCode.code.equals(code)) {
                return moduleCode;
            }
        }
        return null;
    }
}
