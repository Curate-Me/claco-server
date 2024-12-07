package com.curateme.claco.member.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Getter
@AllArgsConstructor
public enum Gender {

	MALE("MALE"), FEMALE("FEMALE");

	private final String gender;

}
