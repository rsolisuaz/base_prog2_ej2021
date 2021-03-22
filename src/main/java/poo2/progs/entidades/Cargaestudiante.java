package poo2.progs.entidades;

import java.util.Objects;


public class Cargaestudiante {
  private static final long serialVersionUID = 1L;

  private long idCargaEstudiante;

  private String matricula;

  private String claveMateria;

  private long idPeriodo;


  public long getIdCargaEstudiante() {
    return idCargaEstudiante;
  }

  public void setIdCargaEstudiante(long idCargaEstudiante) {
    this.idCargaEstudiante = idCargaEstudiante;
  }


  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }


  public String getClaveMateria() {
    return claveMateria;
  }

  public void setClaveMateria(String claveMateria) {
    this.claveMateria = claveMateria;
  }


  public long getIdPeriodo() {
    return idPeriodo;
  }

  public void setIdPeriodo(long idPeriodo) {
    this.idPeriodo = idPeriodo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Cargaestudiante that = (Cargaestudiante) o;
    return idCargaEstudiante == that.idCargaEstudiante;
  }

  @Override
  public int hashCode() {
    return Objects.hash(idCargaEstudiante);
  }

}
