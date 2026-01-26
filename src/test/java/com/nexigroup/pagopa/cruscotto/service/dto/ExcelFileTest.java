package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelFileTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setFileName("test.xlsx");
        excelFile.setContent(new byte[]{1, 2, 3});
        excelFile.setDescription("Test file");

        assertThat(excelFile.getFileName()).isEqualTo("test.xlsx");
        assertThat(excelFile.getContent()).containsExactly(1, 2, 3);
        assertThat(excelFile.getDescription()).isEqualTo("Test file");
    }

    @Test
    void testAllArgsConstructor() {
        byte[] content = {4, 5, 6};
        ExcelFile excelFile = new ExcelFile("file.xlsx", content, "Sample file");

        assertThat(excelFile.getFileName()).isEqualTo("file.xlsx");
        assertThat(excelFile.getContent()).isEqualTo(content);
        assertThat(excelFile.getDescription()).isEqualTo("Sample file");
    }

    @Test
    void testBuilder() {
        byte[] content = {7, 8, 9};
        ExcelFile excelFile = ExcelFile.builder()
            .fileName("builder.xlsx")
            .content(content)
            .description("Builder file")
            .build();

        assertThat(excelFile.getFileName()).isEqualTo("builder.xlsx");
        assertThat(excelFile.getContent()).isEqualTo(content);
        assertThat(excelFile.getDescription()).isEqualTo("Builder file");
    }

    @Test
    void testEqualsAndHashCode() {
        byte[] content = {1, 2, 3};
        ExcelFile file1 = new ExcelFile("file.xlsx", content, "desc");
        ExcelFile file2 = new ExcelFile("file.xlsx", content, "desc");
        ExcelFile file3 = new ExcelFile("other.xlsx", content, "desc");

        assertThat(file1).isEqualTo(file2);
        assertThat(file1).isNotEqualTo(file3);

        assertThat(file1.hashCode()).isEqualTo(file2.hashCode());
        assertThat(file1.hashCode()).isNotEqualTo(file3.hashCode());
    }

    @Test
    void testToString() {
        ExcelFile excelFile = new ExcelFile("file.xlsx", new byte[]{}, "desc");
        String str = excelFile.toString();

        assertThat(str).contains("fileName=file.xlsx");
        assertThat(str).contains("description=desc");
    }
}
