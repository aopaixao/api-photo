package br.com.ekom.photo.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.ekom.photo.dto.FotoDTO;
import br.com.ekom.photo.dto.imgbb.ImgBBDTO;
import br.com.ekom.photo.entity.Foto;
import br.com.ekom.photo.repository.FotoRepository;


@Service
public class FotoService {
	@Autowired
	FotoRepository fotoRepository;
	
	@Value("${imgbb.host.url}")
	private String imgBBHostUrl;
	
	@Value("${imgbb.host.key}")
    private String imgBBHostKey;
	
	public FotoDTO saveDB(
			String foto,
			MultipartFile file
	) throws Exception {
		
		Foto fotoFromStringJson = convertFotoFromStringJson(foto);
		Foto novaFoto = new Foto();
		
		//Verifica se a instancia da entidade recebeu os dados a partir do Json
		if(null != fotoFromStringJson.getNomeImagem()) {
			fotoFromStringJson.setNomeArquivoImagem(file.getOriginalFilename());
			//salva a imagem como byte[]
			fotoFromStringJson.setImagem(file.getBytes());
			//salva a imagem como string base64
			fotoFromStringJson.setImagemBase64(Base64.getEncoder().encodeToString(file.getBytes()));
			novaFoto = fotoRepository.save(fotoFromStringJson);
		}
		
		FotoDTO fotoDTO = new FotoDTO();
		try {
			fotoDTO = saveApiImgBB(file);
		}catch(Exception err) {
			throw new Exception("Ocorreu um erro ao fazer o upload da Foto para a API: " + err.getMessage());
		}
		
		fotoDTO.setIdFoto(novaFoto.getIdFoto());
		fotoDTO.setImagem(novaFoto.getImagem());
		fotoDTO.setImagemBase64(novaFoto.getImagemBase64());
		fotoDTO.setNomeArquivoImagem(novaFoto.getNomeArquivoImagem());
		fotoDTO.setNomeImagem(novaFoto.getNomeImagem());
		
		return fotoDTO;
	}	
	
	public FotoDTO saveApiImgBB(MultipartFile file) throws IOException {
		
		RestTemplate restTemplate = new RestTemplate();
		String serverUrl = imgBBHostUrl + imgBBHostKey;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
		
		ContentDisposition contentDisposition = ContentDisposition
				.builder("form-data")
				.name("image")
				.filename(file.getOriginalFilename())
				.build();
		
		fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
		
		HttpEntity<byte[]> fileEntity = new HttpEntity<>(file.getBytes(), fileMap);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("image", fileEntity);
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity =
				new HttpEntity<>(body, headers);
		
		ResponseEntity<ImgBBDTO> response = null;
		ImgBBDTO imgDTO = new ImgBBDTO();
 
		try {
			response = restTemplate.exchange(
					serverUrl,
					HttpMethod.POST,
					requestEntity,
					ImgBBDTO.class);
			
			imgDTO = response.getBody();
			System.out.println("ImgBBDTO: " + imgDTO.getData().toString());
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
		
		//  Coleta os dados da imagem, após upload via API, e armazena no DTO Foto
		FotoDTO fotoDTO = new FotoDTO();
		if(null != imgDTO) {
			fotoDTO.setTitulo(imgDTO.getData().getTitle());
			fotoDTO.setURL(imgDTO.getData().getUrl());
		}
		
		return fotoDTO;
	}
	
	public List<Foto> getAll() {
		return fotoRepository.findAll();
	}
	
	private Foto convertFotoFromStringJson(String fotoJson) {
		Foto foto = new Foto();
		
		try {
			ObjectMapper objectMapper = 
					new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);

			objectMapper.registerModule(new JavaTimeModule());
			foto = objectMapper.readValue(fotoJson, Foto.class);
		} catch (IOException err) {
			System.out.printf("Ocorreu um erro ao tentar converter a string json para um instância de Foto: " + err.toString());
		}
		
		return foto;
	}
}
