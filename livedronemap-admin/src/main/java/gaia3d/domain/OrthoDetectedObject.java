package gaia3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Cheon JeongDae
 *
 */
@Getter
@Setter
@ToString
public class OrthoDetectedObject {
	
	// controller requestbody 용
	private Integer number;

	// 고유번호
	private Long ortho_detected_object_id;
	// 개별 정사 영상 고유번호
	private Long ortho_image_id;
	// 사용자 아이디
	private String user_id;
	// 객체 타입
	private String object_type;
	// geometry
	private String geometry;
	// 발견일
	private String detected_date;
	// bounding box 공간 정보
	private String bounding_box_geometry;
	// 최대
	private String major_axis;
	// 최소
	private String minor_axis;
	// 방향
	private String orientation;
	// bounding box 면적
	private String bounding_box_area;
	// 길이
	private String length;
	// 속도
	private String speed;
	// 등록일
	private String insert_date;
}
