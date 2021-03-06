/*
 * Copyright (C) 2000 - 2015 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception. You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.viewer;

import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.ResourceLocator;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.silverpeas.test.rule.MockByReflectionRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-viewer.xml")
public class ViewServiceSplitMethodTest extends AbstractViewerTest {

  @Rule
  public MockByReflectionRule reflectionRule = new MockByReflectionRule();

  @Inject
  private ViewService viewService;

  @Before
  public void setup() throws Exception {
    FileUtils.deleteQuietly(new File(FileRepositoryManager.getTemporaryPath()));
    (new File(FileRepositoryManager.getTemporaryPath())).mkdirs();
    final ResourceLocator mockedSettings =
        reflectionRule.mockField(ViewerSettings.class, ResourceLocator.class, "settings");
    when(mockedSettings.getInteger(eq("preview.width.max"), anyInt())).thenReturn(1000);
    when(mockedSettings.getInteger(eq("preview.height.max"), anyInt())).thenReturn(1000);
    when(mockedSettings.getBoolean(eq("viewer.cache.enabled"), anyBoolean())).thenReturn(true);
    when(mockedSettings.getBoolean(eq("viewer.cache.conversion.silent.enabled"), anyBoolean()))
        .thenReturn(false);
    when(mockedSettings.getBoolean(eq("viewer.conversion.strategy.split.enabled"), anyBoolean()))
        .thenReturn(true);
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteQuietly(new File(FileRepositoryManager.getTemporaryPath()));
  }

  @Test
  public void testOdtFileView() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(new ViewerContext("file.pdf", getDocumentNamed("file.odt")));
      assertOfficeOrPdfDocumentView(view);
    }
  }

  @Test
  public void testOdtFileViewFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file.odt")));
      assertOfficeOrPdfDocumentViewWithCacheManagement(view);
    }
  }

  @Test
  public void testPptFileView() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(new ViewerContext("file.pdf", getDocumentNamed("file.ppt")));
      assertPptDocumentView(view);
    }
  }

  @Test
  public void testPptFileViewFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file.ppt")));
      assertPptDocumentViewWithCacheManagement(view);
    }
  }

  @Test
  public void testPdfFileView() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(new ViewerContext("file.pdf", getDocumentNamed("file.pdf")));
      assertOfficeOrPdfDocumentView(view);
    }
  }

  @Test
  public void testPdfFileViewFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view =
          viewService.getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file.pdf")));
      assertOfficeOrPdfDocumentViewWithCacheManagement(view);
    }
  }

  @Test
  public void testPdfFileViewWithSpecialChars() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService.getDocumentView(
          new ViewerContext("file ' - '' .pdf", getDocumentNamed("file ' - '' .pdf")));
      assertOfficeOrPdfDocumentView(view);
    }
  }

  @Test
  public void testPdfFileViewWithSpecialCharsFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService
          .getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file ' - '' .pdf")));
      assertOfficeOrPdfDocumentViewWithCacheManagement(view);
    }
  }

  @Test
  public void testPdfFileViewWithSpaces() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService.getDocumentView(
          new ViewerContext("file with spaces.pdf", getDocumentNamed("file with spaces.pdf")));
      assertOfficeOrPdfDocumentView(view);
    }
  }

  @Test
  public void testPdfFileViewWithSpacesFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService
          .getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file with spaces.pdf")));
      assertOfficeOrPdfDocumentViewWithCacheManagement(view);
    }
  }

  @Test
  public void testPdfFileViewWithQuotes() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService.getDocumentView(
          new ViewerContext("file_with_'_quotes_'.pdf",
              getDocumentNamed("file_with_'_quotes_'.pdf")));
      assertOfficeOrPdfDocumentView(view);
    }
  }

  @Test
  public void testPdfFileViewWithQuotesFromSimpleDocument() throws Exception {
    if (canPerformViewConversionTest()) {
      final DocumentView view = viewService
          .getDocumentView(ViewerContext.from(getSimpleDocumentNamed("file_with_'_quotes_'.pdf")));
      assertOfficeOrPdfDocumentViewWithCacheManagement(view);
    }
  }

  private void assertPptDocumentView(DocumentView view) {
    assertDocumentView(view, 720, 540, false);
  }

  private void assertPptDocumentViewWithCacheManagement(DocumentView view) {
    assertDocumentView(view, 720, 540, true);
  }

  private void assertOfficeOrPdfDocumentView(DocumentView view) {
    assertDocumentView(view, 595, 842, false);
  }

  private void assertOfficeOrPdfDocumentViewWithCacheManagement(DocumentView view) {
    assertDocumentView(view, 595, 842, true);
  }

  @SuppressWarnings("ConstantConditions")
  private void assertDocumentView(DocumentView view, int width, int height,
      final boolean cacheUsed) {
    boolean jsonProcess = JsonPdfToolManager.isActivated();
    assertThat(view, notNullValue());
    int nbFilesAtTempRoot = cacheUsed ? 2 : 1;
    assertThat(new File(FileRepositoryManager.getTemporaryPath()).listFiles(),
        arrayWithSize(nbFilesAtTempRoot));
    assertThat(view.getPhysicalFile().getName(), endsWith("page.swf"));
    assertThat(view.getWidth(), is(String.valueOf(width)));
    assertThat(view.getHeight(), is(String.valueOf(height)));
    assertThat(view.isDocumentSplit(), is(true));
    assertThat(view.areSearchDataComputed(), is(jsonProcess));

    // Files
    int nbPages = view.getNbPages();
    File[] files = view.getPhysicalFile().getParentFile().listFiles();
    int expectedNbTotalFiles = jsonProcess ? (nbPages + 1 + ((nbPages - 1) / 10)) : nbPages;
    assertThat(files.length, is(expectedNbTotalFiles));

    for (File file : files) {
      if (file.getName().startsWith("page.swf")) {
        assertThat(jsonProcess, is(true));
        assertThat(file.getName().replaceFirst("page[.]swf_[0-9]+[.]js", ""), isEmptyString());
      } else if (file.getName().startsWith("page-")) {
        assertThat(file.getName().replaceFirst("page-[0-9]+.swf", ""), isEmptyString());
      } else {
        fail("File '" + file.getPath() + "' is not expected");
      }
    }
  }
}
