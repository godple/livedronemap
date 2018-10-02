package gaia3d.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import gaia3d.domain.GeoserverAPIResult;
import gaia3d.domain.ImageMosaic;
import gaia3d.exception.GeoserverException;
import gaia3d.service.GeoserverService;
import lombok.extern.slf4j.Slf4j;

/**
 * GeoServer 처리 API 
 * @author jskim
 *
 */
@Slf4j
@RequestMapping("/geoserver/")
@RestController
public class GeoserverAPIController {
	
	@Autowired
	private GeoserverService geoserverService;
	
	/**
	 * GeoServer 레이어 확인 
	 * @param request
	 * @param projectId
	 * @return
	 */
	@GetMapping("layers/{projectId:[0-9]+}")
	public ResponseEntity<GeoserverAPIResult> getGeoserverLayer(HttpServletRequest request, @PathVariable("projectId") Long projectId) {
		// TODO 인증 
		
		GeoserverAPIResult aPIResult = new GeoserverAPIResult();
		HttpStatus httpStatus = null; 
		try {
			geoserverService.getGeoserverLayer(projectId);
			
			httpStatus = HttpStatus.OK;
			aPIResult.setStatusCode(httpStatus.value());
		} catch (Exception e) {
			log.warn("", e);
			if (e instanceof GeoserverException) {
				httpStatus = HttpStatus.NOT_FOUND;
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setException(e.getMessage());
			} else {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setException(e.getMessage());
			}
		}
		
		return new ResponseEntity<GeoserverAPIResult>(aPIResult, httpStatus); 
	}
	
	/**
	 * GeoServer 레이어 생성 
	 * @param request
	 * @param imageMosaic
	 * @return
	 */
	@PostMapping("layers")
	public ResponseEntity<GeoserverAPIResult> inputGeoserverLayer(HttpServletRequest request, @RequestBody ImageMosaic imageMosaic) {
		// TODO 인증 
		
		GeoserverAPIResult aPIResult = new GeoserverAPIResult();
		HttpStatus httpStatus = null;
		try {
			Long projectId = imageMosaic.getProject_id();
			if (projectId == null) {
				httpStatus = HttpStatus.BAD_REQUEST;
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setValidationCode("Required field is null.");
			} else {
				geoserverService.inputGeoserverLayer(projectId);
				
				httpStatus = HttpStatus.OK;
				aPIResult.setStatusCode(httpStatus.value());
			}
		} catch (Exception e) {
			log.warn("", e);
			if (e instanceof HttpServerErrorException) {
				httpStatus = ((HttpServerErrorException) e).getStatusCode();
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setException(((HttpServerErrorException) e).getResponseBodyAsString());
			} else {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setException(e.getMessage());
			}
		}
		
		return new ResponseEntity<GeoserverAPIResult>(aPIResult, httpStatus);
	}
	
	/**
	 * GeoServer 서비스 영상 정보 입력 
	 * @param request
	 * @param imageMosaic
	 * @return
	 */
	@PostMapping("images")
	public ResponseEntity<GeoserverAPIResult> insertGeoserverImage(HttpServletRequest request, @RequestBody ImageMosaic imageMosaic) {
		// TODO 인증 
		
		GeoserverAPIResult aPIResult = new GeoserverAPIResult();
		HttpStatus httpStatus = null;
		try {
			if (imageMosaic.getLocation() == null || imageMosaic.getThe_geom() == null 
					|| imageMosaic.getImage_dt() == null || imageMosaic.getProject_id() == null) {
				httpStatus = HttpStatus.BAD_REQUEST;
				aPIResult.setStatusCode(httpStatus.value());
				aPIResult.setValidationCode("Required field is null.");
			} else {
				geoserverService.insertGeoserverImage(imageMosaic);

				httpStatus = HttpStatus.OK;
				aPIResult.setStatusCode(httpStatus.value());
			}
			
		} catch(Exception e) {
			log.warn("", e);
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			aPIResult.setStatusCode(httpStatus.value());
			aPIResult.setException(e.getMessage());
		}
		
		return new ResponseEntity<GeoserverAPIResult>(aPIResult, httpStatus);
	}
	
}
