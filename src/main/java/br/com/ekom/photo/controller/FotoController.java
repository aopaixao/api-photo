package br.com.ekom.photo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.ekom.photo.dto.FotoDTO;
import br.com.ekom.photo.entity.Foto;
import br.com.ekom.photo.service.FotoService;


@RestController
@RequestMapping("/fotos")
public class FotoController {
	@Autowired
	FotoService fotoService;
	
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,
			 MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<FotoDTO> saveFoto(@RequestPart("foto") String foto,
			@RequestPart("source") MultipartFile file) throws IOException{
		
		FotoDTO fotoDTO = new FotoDTO();
		try {
			fotoDTO = fotoService.saveDB(foto, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (null == fotoDTO)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		else
			return new ResponseEntity<>(fotoDTO, HttpStatus.CREATED);

	}
	
	@GetMapping
	public ResponseEntity<List<Foto>> getAllLivrosEntity(){
		return new ResponseEntity<>(fotoService.getAll(), HttpStatus.OK);
	}
}
