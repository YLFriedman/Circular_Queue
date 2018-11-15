package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;

import static org.junit.Assert.assertEquals;

public class DbUtilFunctionTest {

    @Test
    public void key_sanitization_validation() {
        /*assertEquals("getSanitizedKey failed - Case", "capstest", DbUtil.getSanitizedKey("CapsTest"));
        assertEquals("getSanitizedKey failed - Space", "space_test", DbUtil.getSanitizedKey("space test"));
        assertEquals("getSanitizedKey failed - Tab", "tab_test", DbUtil.getSanitizedKey("tab\ttest"));
        assertEquals("getSanitizedKey failed - Special Characters", "_pecialch_rs", DbUtil.getSanitizedKey("$pecialch@rs"));
        assertEquals("getSanitizedKey failed - Numbers", "numb3rs", DbUtil.getSanitizedKey("numb3rs"));
        assertEquals("getSanitizedKey failed - No Change", "nochange", DbUtil.getSanitizedKey("nochange"));
        assertEquals("getSanitizedKey failed - Mixed", "my_u_er_nam3", DbUtil.getSanitizedKey("My U$er Nam3"));*/
    }

}
