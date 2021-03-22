package poo2.progs.entidades;


public class Cargaprofesor {

  private long idCargaProfesor;
  private String rfc;
  private String claveMateria;
  private long idPeriodo;


  public long getIdCargaProfesor() {
    return idCargaProfesor;
  }

  public void setIdCargaProfesor(long idCargaProfesor) {
    this.idCargaProfesor = idCargaProfesor;
  }


  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
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

}
