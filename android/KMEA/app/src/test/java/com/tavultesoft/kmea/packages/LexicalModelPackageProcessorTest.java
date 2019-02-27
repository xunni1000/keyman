package com.tavultesoft.kmea.packages;

import com.tavultesoft.kmea.KMManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class LexicalModelPackageProcessorTest {
  private static final File TEST_RESOURCE_ROOT = new File("test_resources");
  private static final File TEST_EXTRACTION_ROOT = new File(TEST_RESOURCE_ROOT, "temp");

  private static final String TEST_EN_CUSTOM_MODEL_KMP_NAME = "example.en.custom";
  private static final File TEST_EN_CUSTOM_MODEL_KMP_FILE = new File(TEST_RESOURCE_ROOT, "packages" +
    File.separator + "en.custom" + File.separator + TEST_EN_CUSTOM_MODEL_KMP_NAME + ".model.kmp");
  private static final File TEST_EN_CUSTOM_MODEL_KMP_TARGET = new File(TEST_EXTRACTION_ROOT, "models" +
    File.separator + TEST_EN_CUSTOM_MODEL_KMP_NAME);

  private static File tempPkg;

  // Each test gets a fresh version of the extracted package.
  @Before
  public void extractBaseTestPackage() {
    PackageProcessor.initialize(TEST_EXTRACTION_ROOT);
    try {
      tempPkg = LexicalModelPackageProcessor.unzipKMP(TEST_EN_CUSTOM_MODEL_KMP_FILE);
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /**
   * Post-test cleanup.  While the temp/ directory is .gitignore'd, this provides an extra layer
   * of safety from polluting the repo file path.
   * @throws IOException
   */
  @After
  public void eraseTestPackages() throws IOException {
    FileUtils.deleteDirectory(tempPkg);
    FileUtils.deleteDirectory(TEST_EN_CUSTOM_MODEL_KMP_TARGET);
  }

  // Lexical Model Package Tests

  @Test
  public void test_getPackageTarget() {
    JSONObject json = PackageProcessor.loadPackageInfo(tempPkg);

    Assert.assertEquals(PackageProcessor.PP_LEXICAL_MODELS_KEY, PackageProcessor.getPackageTarget(json));
  }

  @Test
  public void test_load_EN_CUSTOM_models() throws Exception {
    JSONObject json = PackageProcessor.loadPackageInfo(tempPkg);
    FileUtils.moveDirectory(tempPkg, TEST_EN_CUSTOM_MODEL_KMP_TARGET);

    Assert.assertNotNull(json);

    Map<String, String>[] models = LexicalModelPackageProcessor.processLexicalModelsEntry(json.getJSONArray("lexicalModels").getJSONObject(0), "example.en.custom");

    HashMap<String, String> en_custom = new HashMap<String, String>();
    en_custom.put(KMManager.KMKey_PackageID, "example.en.custom");
    en_custom.put(KMManager.KMKey_LexicalModelName, "Example (English) Template Custom Model");
    en_custom.put(KMManager.KMKey_LexicalModelID, "example.en.custom");
    en_custom.put(KMManager.KMKey_LexicalModelVersion, "1.0.0");
    en_custom.put(KMManager.KMKey_LanguageID, "en");
    en_custom.put(KMManager.KMKey_LanguageName, "English");

    Assert.assertEquals(en_custom, models[0]);

    // Lexical model same, but different language pairing
    en_custom.put(KMManager.KMKey_LanguageID, "en-us");
    en_custom.put(KMManager.KMKey_LanguageName, "English (US)");

    Assert.assertEquals(en_custom, models[1]);
  }

  @Test
  public void test_kmpProcessLexicalModel() throws Exception {
    List<Map<String, String>> installedModels = LexicalModelPackageProcessor.processKMP(TEST_EN_CUSTOM_MODEL_KMP_FILE, true, true);
  }
}
