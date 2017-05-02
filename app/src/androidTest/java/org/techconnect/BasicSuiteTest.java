package org.techconnect;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

/**
 * Created by doranwalsten on 4/27/17.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest.class
        , DownloadGuideTest.class
        //, SessionTest.class
        , RepairHistoryTest.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicSuiteTest {
}
