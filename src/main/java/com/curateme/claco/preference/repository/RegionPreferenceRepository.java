package com.curateme.claco.preference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.preference.domain.entity.RegionPreference;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 */
public interface RegionPreferenceRepository extends JpaRepository<RegionPreference, Long> {
}
