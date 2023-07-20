package br.com.ekom.photo.entity;

import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "foto")
@JsonIdentityInfo(
	generator = ObjectIdGenerators.PropertyGenerator.class,
	property = "idFoto",
	scope = Foto.class
)
public class Foto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_foto")
	private Integer idFoto;

	@Column(name = "nome_imagem")
	private String nomeImagem;
	
	@Column(name = "nome_arquivo_imagem")
	private String nomeArquivoImagem;

	//@Lob
	//@JdbcTypeCode(Types.BINARY)
	@JdbcTypeCode(Types.BLOB)
	private byte[] imagem;

	@Lob	
	@Column(name = "imagem_base64", columnDefinition="BLOB")
	private String imagemBase64;

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}
	
	public String getNomeImagem() {
		return nomeImagem;
	}

	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

	public String getNomeArquivoImagem() {
		return nomeArquivoImagem;
	}

	public void setNomeArquivoImagem(String nomeArquivoImagem) {
		this.nomeArquivoImagem = nomeArquivoImagem;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public String getImagemBase64() {
		return imagemBase64;
	}

	public void setImagemBase64(String imagemBase64) {
		this.imagemBase64 = imagemBase64;
	}
}