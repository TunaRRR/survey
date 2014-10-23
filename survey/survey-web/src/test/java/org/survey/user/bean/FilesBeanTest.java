package org.survey.user.bean;

import java.io.IOException;

import junit.framework.Assert;
import lombok.experimental.Delegate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.survey.file.model.File;
import org.survey.file.repository.FileRepository;
import org.survey.file.service.FileService;

/**
 * Test FilesBean. Must use SpringJUnit4ClassRunner to enable spring injection.
 * Loaded Spring configuration is defined by ContextConfiguration.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class FilesBeanTest {
//	@Autowired
	FilesBeanMock filesBean;
	@Autowired
	FileRepository fileRepository;
	@Autowired
	private FileService fileService;
	private File savedFile;

	@Before
	public void setUp() {
	    filesBean = new FilesBeanMock();
	    filesBean.setFileService(fileService);
		savedFile = fileRepository.save(new File("filename", "mimeType",
				"content".getBytes()));
	}

	@After
	public void tearDown() {
		fileRepository.deleteAll();
	}

    @Test
    public void getImage() throws IOException {
        filesBean.setRequestParameter(savedFile.getId().toString());
        StreamedContent streamedContent = filesBean.getImage();
        Assert.assertNotNull(streamedContent);
        Assert.assertNotNull(streamedContent.getStream());
//        Assert.assertEquals("png", streamedContent.getContentType());
//        Assert.assertEquals("email", streamedContent.getName());
    }

    @Test
    public void paintWithNullData() throws IOException {
        filesBean.setRequestParameter(null);
        StreamedContent streamedContent = filesBean.getImage();
        Assert.assertNull(streamedContent);
    }
    public class FilesBeanMock extends FilesBean {
        @Delegate
        BeanTestHelper beanTestHelper = new BeanTestHelper();

        @Override
        protected String getRequestParameter(String parameterName) {
            return beanTestHelper.getRequestParameter(parameterName);
        }
    }
}