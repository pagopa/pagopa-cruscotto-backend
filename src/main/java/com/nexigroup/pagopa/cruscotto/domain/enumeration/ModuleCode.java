package com.nexigroup.pagopa.cruscotto.domain.enumeration;

public enum ModuleCode {
    A1("A.1"),
    A2("A.2"),
    B2("B.2");

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
