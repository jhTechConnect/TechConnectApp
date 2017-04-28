package org.techconnect;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by doranwalsten on 4/27/17.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest.class,
        DownloadGuideTest.class,
        SessionTest.class
})

public class BasicSuiteTest {
}
