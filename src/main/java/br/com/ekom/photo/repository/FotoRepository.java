package br.com.ekom.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ekom.photo.entity.Foto;

public interface FotoRepository extends JpaRepository<Foto, Integer> {

}
