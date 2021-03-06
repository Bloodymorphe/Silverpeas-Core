/*
 * Copyright (C) 2000 - 2016 Silverpeas
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
package com.silverpeas.jcrutil;

import org.apache.jackrabbit.webdav.util.EncodeUtil;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.silverpeas.jcrutil.SilverpeasJcrWebdavContext.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.silverpeas.cache.service.CacheServiceFactory.getApplicationCacheService;

/**
 * @author Yohann Chastagnier
 */
public class SilverpeasJcrWebdavContextTest {

  private static final String AUTH_TOKEN = "tokenWith16Chars";
  private static final String FILENAME_WITH_SPECIAL_CHARS =
      "toto&é'()@ç+^i=¨*µ%ù!§:;.,?&&&?{}][`.doc";

  @Before
  public void setup() {
    getApplicationCacheService().clear();
    getApplicationCacheService().put(WEBDAV_JCR_URL_SUFFIX + "dummy", "dummy");
  }

  @After
  public void tearDown() {
    assertThat(getApplicationCacheService().get(WEBDAV_JCR_URL_SUFFIX + "dummy", String.class),
        is("dummy"));
    getApplicationCacheService().clear();
  }

  @Test
  public void shouldClearTheCache() {
    assertThat(getApplicationCacheService().get(WEBDAV_JCR_URL_SUFFIX + AUTH_TOKEN), nullValue());

    createWebdavContext("/webdav/document/" + FILENAME_WITH_SPECIAL_CHARS, AUTH_TOKEN)
        .getWebDavUrl();

    assertThat(getApplicationCacheService().get(WEBDAV_JCR_URL_SUFFIX + AUTH_TOKEN, String.class),
        is("document/" + EncodeUtil.escape(FILENAME_WITH_SPECIAL_CHARS)));

    SilverpeasJcrWebdavContext.clearFromToken(AUTH_TOKEN);

    assertThat(getApplicationCacheService().get(WEBDAV_JCR_URL_SUFFIX + AUTH_TOKEN), nullValue());
  }

  @Test
  public void shouldBeWellEncoded() {
    String expectedWebdavUrl =
        "/webdav/" + AUTH_TOKEN + "/" + EncodeUtil.escape(FILENAME_WITH_SPECIAL_CHARS);

    SilverpeasJcrWebdavContext context =
        createWebdavContext("/webdav/document/" + FILENAME_WITH_SPECIAL_CHARS, AUTH_TOKEN);
    assertThat(context.getJcrDocumentUrlLocation(),
        is("/webdav/document/" + FILENAME_WITH_SPECIAL_CHARS));
    assertThat(context.getToken(), is(AUTH_TOKEN));
    assertThat(context.getWebDavUrl(), is(expectedWebdavUrl));
  }

  @Test
  public void shouldBeWellDecoded() {
    String webdavUrl =
        createWebdavContext("/webdav/document/" + FILENAME_WITH_SPECIAL_CHARS, AUTH_TOKEN)
            .getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    assertThat(context.getJcrDocumentUrlLocation(),
        is("/webdav/document/" + EncodeUtil.escape(FILENAME_WITH_SPECIAL_CHARS)));
    assertThat(EncodeUtil.unescape(context.getJcrDocumentUrlLocation()),
        is("/webdav/document/" + FILENAME_WITH_SPECIAL_CHARS));
    assertThat(context.getToken(), is(AUTH_TOKEN));
    assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void parentPathShouldBeWellDecoded() {
    String webdavUrl = createWebdavContext("/webdav/document/a", AUTH_TOKEN).getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    assertThat(context.getJcrDocumentUrlLocation(), is("/webdav/document/a"));
    assertThat(context.getToken(), is(AUTH_TOKEN));
    assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void otherParentPathThatShouldAlsoBeWellDecoded() {
    String webdavUrl = createWebdavContext("/webdav/document", AUTH_TOKEN).getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    assertThat(context.getJcrDocumentUrlLocation(), is("/webdav/document"));
    assertThat(context.getToken(), is(AUTH_TOKEN));
    assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void shouldBeAlsoWellDecoded() {
    String webdavUrl =
        createWebdavContext("/webdav/document/dummyFileName", AUTH_TOKEN).getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    assertThat(context.getJcrDocumentUrlLocation(), is("/webdav/document/dummyFileName"));
    assertThat(context.getToken(), is(AUTH_TOKEN));
    assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void directoryPathShouldBeWellDecoded() {
    String webdavUrl = createWebdavContext("/repo/webdav/filename.xlsx", AUTH_TOKEN).getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/filename.xlsx"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/" + AUTH_TOKEN;
    context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/" + AUTH_TOKEN + "/";
    context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void noDecodingWhenNoToken() {
    String webdavUrl = "/repo/webdav/without/token/filename.xlsx";

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(),
        is("/repo/webdav/without/token/filename.xlsx"));
    MatcherAssert.assertThat(context.getToken(), isEmptyString());
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/without/token";
    context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/without/token"));
    MatcherAssert.assertThat(context.getToken(), isEmptyString());
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/without/token/";
    context = getWebdavContext(webdavUrl);
    MatcherAssert
        .assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/without/token/"));
    MatcherAssert.assertThat(context.getToken(), isEmptyString());
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));
  }

  @Test
  public void decodingWithSuccessWhenItExistsSeveralWebdavTokens() {
    String webdavUrl =
        createWebdavContext("/repo/webdav/a/b/webdav.xlsx", AUTH_TOKEN).getWebDavUrl();

    SilverpeasJcrWebdavContext context = getWebdavContext(webdavUrl);
    MatcherAssert
        .assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/a/b/webdav.xlsx"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/" + AUTH_TOKEN;
    context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/a/b"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));

    webdavUrl = "/repo/webdav/" + AUTH_TOKEN + "/";
    context = getWebdavContext(webdavUrl);
    MatcherAssert.assertThat(context.getJcrDocumentUrlLocation(), is("/repo/webdav/a/b/"));
    MatcherAssert.assertThat(context.getToken(), is(AUTH_TOKEN));
    MatcherAssert.assertThat(context.getWebDavUrl(), is(webdavUrl));
  }
}