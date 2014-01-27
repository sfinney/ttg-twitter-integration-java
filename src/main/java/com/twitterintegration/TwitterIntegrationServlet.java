package com.twitterintegration;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.schema.Person; */

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterIntegrationServlet extends HttpServlet {
	
	private static final String ACCESS_TOKEN = "2291204155-74hZw6tJATnOnGidwlLvMLMCuoI0hv1gHveRaab";
	private static final String ACCESS_TOKEN_SECRET = "4NL4979d3FQNTIzhsmsorkQ0MzaMhcUi4T9HeNTZhGFvS";
	
	private static final String CONSUMER_KEY = "hHKbEpoT86DcFwuvZ0B8JQ";
	private static final String CONSUMER_SECRET = "J2LhRTHWlVcVta7E7BFGvZNy0h6ZVcZGFf5d97R0ERI";
	
	// private static final String REQUEST_PARAMETER = "linkedInHandle";
	
	private static final String REQUEST_PARAMETER = "twitterHandle";
	
	private String handle;	
	private String profileImageURL;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// this.handle = request.getParameter(REQUEST_PARAMETER);
		
		this.handle = URLEncoder.encode("http://ie.linkedin.com/pub/simon-finney/60/a0b/574", "UTF8");
		
		try {
			getLinkedInUserDetails();
			
		} catch (ParserConfigurationException error) {
			
			// TODO Autogenerated catch block
			error.printStackTrace();
			
		} catch (SAXException error) {
			
			// TODO Autogenerated catch block
			error.printStackTrace();
		}
		
        /* try {
        	getTwitterUserDetails();
        	
        } catch (TwitterException error) {
        	
        	// TODO Autogenerated catch block
        	error.printStackTrace();
        } */
        
        ServletOutputStream output = response.getOutputStream();
        
        output.write(profileImageURL.getBytes());
        
        /* URL url = new URL(profileImageURL);        
        URLConnection urlConnection = url.openConnection();
        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());        
        
        byte[] byteBuffer = new byte[1024];
        int inputStreamLength;
        
        while ((inputStreamLength = bufferedInputStream.read(byteBuffer)) > 0) {
        
        	output.write(byteBuffer, 0, inputStreamLength);
        } */
        
        output.flush();
        output.close();
    }
	
	private void getLinkedInUserDetails() throws ParserConfigurationException, SAXException {		
		
		String apiKey = "7728b7mz21og75";
		String secretKey = "lsq6b6rSMQTX9ReG";
		
		String oAuthUserToken = "aaf71121-02b8-41b6-b049-d232e2222dd7";
		String oAuthUserSecret = "ab2bc414-5054-4f7d-a419-d1a1e82f0ff6";
		
		String url = "http://api.linkedin.com/v1/people/url=" + handle + "/picture-urls::(original)";

		// linkedin-j implementation
		
		/* LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(apiKey, secretKey);
		LinkedInApiClient client = factory.createLinkedInApiClient(oAuthUserToken, oAuthUserSecret);
		
		Person person = client.getProfileById(handle);		
		this.profileImageURL = person.getPictureUrl(); */
		
		// Scribe implementation		
		OAuthService oAuthService = new ServiceBuilder().provider(LinkedInApi.class)
														.apiKey(apiKey)
														.apiSecret(secretKey)
														.build();
		OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, url);
		oAuthService.signRequest(new Token(oAuthUserToken, oAuthUserSecret), oAuthRequest);
		
		Response response = oAuthRequest.send();
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		
		/* DefaultHandler defaultHandler = new DefaultHandler() {
			
			boolean pictureURL = false;
			
			private void startElement(String uri, String localName, String qName) throws SAXException { }
			
			public void endElement(String uri, String localName, String qName) throws SAXException { }
			
			public void characters(char[] character, int start, int length) throws SAXException {
				this.profileImageURL = String.copyValueOf(char, start, length).trim();
			}			
		}
		
		this.profileImageURL = saxParser.parse(response.getBody(), defaultHandler); */
		
		this.profileImageURL(response.getBody());
	}
	
	private void getTwitterUserDetails() throws TwitterException {
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
		
		Configuration configuration = configurationBuilder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
		
		AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		
		twitter.setOAuthAccessToken(accessToken);
		
		User user = twitter.showUser(handle);		
		this.profileImageURL = user.getOriginalProfileImageURL();
	}
}