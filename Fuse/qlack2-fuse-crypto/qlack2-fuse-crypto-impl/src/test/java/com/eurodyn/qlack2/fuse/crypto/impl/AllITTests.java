package com.eurodyn.qlack2.fuse.crypto.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  CryptoServiceImplTest.class
})
public class AllITTests {

  @BeforeClass
  public static void beforeClass() throws ClassNotFoundException {
  }

  @AfterClass
  public static void afterClass() {
  }

}