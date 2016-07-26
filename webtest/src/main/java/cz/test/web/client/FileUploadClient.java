package cz.test.web.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class FileUploadClient {

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:6500/matiweb/api/").build("");
	}

	public static void main(String[] args) throws FileNotFoundException {
		final ClientConfig config = new DefaultClientConfig();
		final Client client = Client.create(config);

		final WebResource resource = client.resource(getBaseURI()).path("v1/testsuite/zip");

		final File fileToUpload = new File("/Users/cz/dejavu.sql");

		final FormDataMultiPart multiPart = new FormDataMultiPart();
		if (fileToUpload != null) {
			multiPart.bodyPart(new FileDataBodyPart("file", fileToUpload, MediaType.APPLICATION_OCTET_STREAM_TYPE));
		}

		final ClientResponse clientResp = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class,
				multiPart);
		System.out.println("Response: " + clientResp.getClientResponseStatus());

		client.destroy();
	}
}
