package gaia3d.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import gaia3d.domain.ImageMosaic;
import gaia3d.domain.Policy;
import gaia3d.exception.GeoserverException;
import gaia3d.persistence.GeoserverMapper;
import gaia3d.security.Crypt;
import gaia3d.service.GeoserverService;
import gaia3d.service.PolicyService;

@Service
public class GeoserverServiceImpl implements GeoserverService {
	
	@Autowired
	private GeoserverMapper geoserverMapper;
	
	@Autowired
	private PolicyService policyService;
	
	@Override
	public Long getGeoserverLayer(Long projectId) {
		// TODO policy 연결 
		// http://localhost:8080/geoserver/rest/workspaces/dronemap/coveragestores/dronemap/coverages/dronemap_t.json
		Policy policy = policyService.getPolicy();
		String geoserverDataUrl = policy.getGeoserver_data_url();
		String geoserverDataWorkspace = policy.getGeoserver_data_workspace();
		
		String layerName = String.format("%s_%d", geoserverDataWorkspace, projectId);
		String url = String.format("%s/rest/workspaces/%s/coveragestores/%s/coverages/%s", 
				geoserverDataUrl, geoserverDataWorkspace, geoserverDataWorkspace, layerName);
		
		// set haeder
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String user = policy.getGeoserver_user();
		String password = policy.getGeoserver_password();
		String encodedUserPassword = encodeUserPassword(user, password);
	    headers.add("Authorization", "Basic " + encodedUserPassword);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new GeoserverException(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			throw new GeoserverException(e.getResponseBodyAsString());
		}

		return projectId;
	}

	@Override
	public Long inputGeoserverLayer(Long projectId) {
		
		// TODO policy 연결 
		// http://localhost:8080/geoserver/rest/workspaces/dronemap/coveragestores/dronemap/coverages
		Policy policy = policyService.getPolicy();
		String geoserverDataUrl = policy.getGeoserver_data_url();
		String geoserverDataWorkspace = policy.getGeoserver_data_workspace();
		
		String url = String.format("%s/rest/workspaces/%s/coveragestores/%s/coverages", 
				geoserverDataUrl, geoserverDataWorkspace, geoserverDataWorkspace);
		
		String layerInfo = null;
		try {
			layerInfo = new String(Files.readAllBytes(Paths.get("src/main/resources/geoserver/layer.json")), StandardCharsets.UTF_8);
			layerInfo = layerInfo.replace("{projectId}", String.valueOf(projectId));
			layerInfo = layerInfo.replace("{workspaceName}", geoserverDataWorkspace);

			// set haeder
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String user = policy.getGeoserver_user();
			String password = policy.getGeoserver_password();
			String encodedUserPassword = encodeUserPassword(user, password);
		    headers.add("Authorization", "Basic " + encodedUserPassword);
			HttpEntity<String> requestEntity =  new HttpEntity<String>(layerInfo, headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
			
			if (response.getStatusCode() != HttpStatus.CREATED) {
				throw new GeoserverException(response.getBody());
			}
			
		} catch (HttpClientErrorException e) {
			throw new GeoserverException(e.getResponseBodyAsString());
		} catch (IOException e) {
			throw new GeoserverException(e.getMessage());
		}
		
		return projectId;
	}

	@Override
	@Transactional
	public int insertGeoserverImage(ImageMosaic imageMosaic) {
		return geoserverMapper.insertGeoserverImage(imageMosaic);
	}
	
	private String encodeUserPassword(String user, String password) {
		String plainUserPassword = Crypt.decrypt(user) + ":" + Crypt.decrypt(password);
		return Base64.getEncoder().encodeToString(plainUserPassword.getBytes());
	}
	
//	private HttpHeaders createHttpHeaders(String user, String password)
//	{
//	    String notEncoded = user + ":" + password;
//	    String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    headers.add("Authorization", encodedAuth);
//	    return headers;
//	}

}
