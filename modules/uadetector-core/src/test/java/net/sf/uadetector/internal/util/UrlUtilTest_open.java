package net.sf.uadetector.internal.util;

import java.io.IOException;
import java.net.URL;

import net.sf.uadetector.exception.CanNotOpenStreamException;

import org.easymock.EasyMock;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UrlUtil.class)
@PowerMockIgnore({ "org.slf4j.*" })
public class UrlUtilTest_open {

	private static final Logger LOG = LoggerFactory.getLogger(UrlUtilTest_open.class);

	@Before
	public void doNotRunOnLinux() {
		// this assumption currently does not work
		final boolean isLinux = OperatingSystemDetector.isLinux();
		Assume.assumeTrue(isLinux);
		if (isLinux) {
			LOG.info("This unit test will be ignored due to a bug in EasyMock <= 3.1, in the class mocking feature under GNU/Linux.");
		}
	}

	@Test
	// (expected = CanNotOpenStreamException.class)
	public void open_withIOException() throws IOException {
		if (!OperatingSystemDetector.isLinux()) {
			try {
				// The following line makes a bug in EasyMock 3.1 visible. For example on OS X the output will be
				// 'EasyMock.DISABLE_CLASS_MOCKING: false' and on Linux it is 'EasyMock.DISABLE_CLASS_MOCKING: true'.
				// The behavior of EasyMock on Linux breaks this test.
				EasyMock.setEasyMockProperty(EasyMock.DISABLE_CLASS_MOCKING, Boolean.FALSE.toString());
				System.out.println("EasyMock.DISABLE_CLASS_MOCKING: " + EasyMock.getEasyMockProperty(EasyMock.DISABLE_CLASS_MOCKING));

				final URL url = PowerMock.createMock(URL.class);
				EasyMock.expect(url.openStream()).andThrow(new IOException());
				PowerMock.replayAll();
				UrlUtil.open(url);
				PowerMock.verifyAll();
			} catch (CanNotOpenStreamException e) {
				// all things okay
			}
		}
	}

}
