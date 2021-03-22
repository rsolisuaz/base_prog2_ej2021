package poo2.prac09.main;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dbunit.*;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.*;
import poo2.ConfigAccesoBaseDatos;
import poo2.progs.basedatos.DaoEscolaresREST;
import poo2.progs.entidades.Estudiante;
import poo2.progs.entidades.Municipio;
import poo2.progs.interfaces.DaoEscolares;
import poo2.progs.utils.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestServiciosREST extends TestCase {
    private static IDatabaseTester databaseTester;
    private final static String driverName="com.mysql.cj.jdbc.Driver";
    private static IDatabaseConnection conndbunit;
    private static int calif_estudiante[]={0,0,0,0};
    private static double calif_municipio;
    private final static int CALIF_OBTENER=10;
    private final static int CALIF_AGREGAR=5;
    private final static int CALIF_UPDATE=5;
    private final static int CALIF_DELETE=5;
    private final static int CALIF_MUNICIPIO=20;
    private final static int MAX_CALIF_ESTUDIANTE =55;
    private final static double PORCENTAJE_ESTUDIANTE =80.0;
    private final static double PORCENTAJE_MUNICIPIO=20.0;
    private static String urlbaseGF;
    private static String nombreCompleto;
    private static String ubicacionGF;
    private static String puertoGF;
    private static DaoEscolares dao;

    @BeforeAll
    public static void inicializa() throws Exception {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
        ResourceBundle res=ResourceBundle.getBundle("datosmysql", Locale.ROOT);
        String matricula=res.getString("matricula");
        nombreCompleto=res.getString("nombre");

        String usuario=res.getString("usuario");
        String clave= res.getString("clave");
        String basedatos=res.getString("basedatos");

        String tipo_prueba=System.getProperty("tipo_prueba","local");
        if (tipo_prueba==null || tipo_prueba.isEmpty()) {
            tipo_prueba="local";
        }

        String ubicacionmysl=res.getString("ubicacionmysql"+"_"+tipo_prueba);
        String puertomysql=res.getString("puertomysql"+"_"+tipo_prueba);

        ubicacionGF=res.getString("ubicacionglassfish"+"_"+tipo_prueba) ;
        puertoGF=res.getString("puertoglassfish"+"_"+tipo_prueba);

        urlbaseGF= String.format("http://%s:%s/RESTControlEscolar-%s",
                ubicacionGF,puertoGF,matricula);
        String url=String.format("jdbc:mysql://%s:%s/%s",
                ubicacionmysl,puertomysql,basedatos);
        //System.out.println("URL MySQL:"+url);

        String pagina= HttpUtils.httpGet(urlbaseGF, null);
        int posInicioTitulo=pagina.indexOf("<title>")+7;
        int posFinTitulo=pagina.indexOf("</title>");
        int posInicioH1=pagina.indexOf("<h1>")+4;
        int posFinH1=pagina.indexOf("</h1>");
        int posInicioH2=pagina.indexOf("<h2>")+4;
        int posFinH2=pagina.indexOf("</h2>");
        String textoTitulo=pagina.substring(posInicioTitulo, posFinTitulo).toUpperCase();
        String textoH1=pagina.substring(posInicioH1, posFinH1).toUpperCase();
        String textoH2=pagina.substring(posInicioH2, posFinH2).toUpperCase();
        String tituloEsperado=String.format("Servicios REST de %s",nombreCompleto).toUpperCase();
        String h2Esperado=String.format("Matricula %s",matricula).toUpperCase();

        assertEquals(tituloEsperado,textoTitulo);
        assertEquals(tituloEsperado,textoH1);
        assertEquals(h2Esperado,textoH2);

        dao=new DaoEscolaresREST(ubicacionGF,Integer.parseInt(puertoGF),matricula);

        databaseTester=new JdbcDatabaseTester(driverName,url,
                usuario,clave);
        databaseTester.setOperationListener(new ConfigAccesoBaseDatos.CustomConfigurationOperationListener());
        conndbunit=databaseTester.getConnection();
        DatabaseConfig config=conndbunit.getConfig();
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS,true);
        IDataSet dataSet=new FlatXmlDataSetBuilder().build(new FileInputStream("datosescolarv2.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.onSetup();
    }

    private static double puntaje(int[] arreglo, int[] numpruebas, double[] puntos) {
        double suma=0.0;
        for (int i=0; i<arreglo.length;i++) {
            if (numpruebas[i]!=0 && numpruebas[i]==arreglo[i]) {
                suma += puntos[i];
            }
        }
        return suma;
    }

    @AfterAll
    public static void termina() throws Exception {
        databaseTester.setTearDownOperation(DatabaseOperation.REFRESH);
        databaseTester.onTearDown();
        double[] puntajes_porop={5.0, 25.0, 25.0, 25.0};
        int[] numpruebas={1,3,3,3};
        double puntuacion=puntaje(calif_estudiante,numpruebas,puntajes_porop);
        System.out.println("Para estudiantes puntuacion="+puntuacion);
        System.out.println("Para municipio puntuacion="+calif_municipio);
        double calif_total = puntuacion;
        System.out.printf("Puntos para Pruebas ServiciosREST (Estudiante): %.2f/%.2f\n",puntuacion,PORCENTAJE_ESTUDIANTE);
        calif_total +=calif_municipio;
        System.out.printf("Puntos para Pruebas ServiciosREST (obtenMunicipios): %.2f/%.2f\n", calif_municipio, PORCENTAJE_MUNICIPIO);
        System.out.printf("Calificacion Total para Practica 9: %.2f/100.00\n",calif_total);
    }

    /// ESTUDIANTE

    private void comparaEstudiante(Estudiante actual, ITable expected, int numrow) {
        try {
            assertEquals(actual.getMatricula(), expected.getValue(numrow, "matricula"));
            assertEquals(actual.getNombre(), expected.getValue(numrow, "nombre"));
            assertEquals(actual.getApPaterno(), expected.getValue(numrow, "ap_paterno"));
            assertEquals(actual.getApMaterno(), expected.getValue(numrow, "ap_materno"));
            assertEquals(actual.getCalle(), expected.getValue(numrow, "calle"));
            assertEquals(actual.getColonia(), expected.getValue(numrow, "colonia"));
            assertEquals(actual.getCodPostal(), expected.getValue(numrow, "cod_postal"));
            assertEquals(actual.getTelefono(), expected.getValue(numrow, "telefono"));
            assertEquals(actual.getEmail(), expected.getValue(numrow, "email"));
            assertEquals(String.valueOf(actual.getIdEstado()), expected.getValue(numrow, "id_estado").toString());
            assertEquals(String.valueOf(actual.getIdMunicipio()), expected.getValue(numrow, "id_municipio").toString());
        }
        catch (Exception ex) {
            //ex.printStackTrace();
            assertNull("No deberia generar excepcion comparar los estudiantes",ex);
        }
    }


    @Test
    @Order(1)
    public void testEstudianteObten() throws Exception {
        List<Estudiante> actual;
        try {
            actual=dao.obtenEstudiantes();
        }
        catch (Exception ex) {
            actual=new ArrayList<>();
            assertNull("No deberia generar excepcion el metodo obtenEstudiantes de DaoEscolaresREST",ex);
        }
        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("datosescolarv2.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");

        assertEquals(actual.size(),expectedTable.getRowCount());
        for (int i=0; i<actual.size(); i++) {
            comparaEstudiante(actual.get(i), expectedTable,i);
        }
        calif_estudiante[0]++;
    }

    @Test
    @Order(2)
    public void testEstudianteAgregaValido() throws Exception {
        String matricula="31081514";
        String nombre="Miguel";
        String apPaterno="Salas";
        String apMaterno="Guzman";
        String calle="Herreros 25";
        String colonia="Centro";
        String codpostal="99104";
        String telefono="4331234567";
        String email="misalas@uaz.edu.mx";
        long idest=32L;
        long idmun=32042L;

        Estudiante est = new Estudiante(matricula,nombre,apPaterno,email);
        est.setApMaterno(apMaterno);
        est.setCalle(calle);
        est.setColonia(colonia);
        est.setCodPostal(codpostal);
        est.setTelefono(telefono);
        est.setIdEstado(idest);
        est.setIdMunicipio(idmun);

        boolean resultado;
        try {
            resultado = dao.agregaEstudiante(est);
        }
        catch (Exception ex) {
            resultado=false;
            assertNull("No deberia generar excepcion el metodo agregaEstudiante de DaoEscolaresREST",ex);
        }
        assertTrue(resultado);


        ITable actualTable=conndbunit.createTable("estudiante");

        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("estudiante_add.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");

        Assertion.assertEquals(expectedTable,actualTable);
        calif_estudiante[1]++;
    }

    @Test
    @Order(3)
    public void testEstudianteAgregaDuplicado() throws Exception {
        String matricula="31081514";
        String nombre="Miguel";
        String apPaterno="Salas";
        String apMaterno="Guzman";
        String calle="Herreros 25";
        String colonia="Centro";
        String codpostal="99104";
        String telefono="4331234567";
        String email="misalas@uaz.edu.mx";
        long idest=32L;
        long idmun=32042L;

        Estudiante est = new Estudiante(matricula,nombre,apPaterno,email);
        est.setApMaterno(apMaterno);
        est.setCalle(calle);
        est.setColonia(colonia);
        est.setCodPostal(codpostal);
        est.setTelefono(telefono);
        est.setIdEstado(idest);
        est.setIdMunicipio(idmun);

        boolean resultado;
        try {
            resultado = dao.agregaEstudiante(est);
        }
        catch (Exception ex) {
            resultado=false;
            assertNull("No deberia generar excepcion el metodo agregaEstudiante de DaoEscolaresREST",ex);
        }
        assertFalse(resultado);


        ITable actualTable=conndbunit.createTable("estudiante");

        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("estudiante_add.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");

        Assertion.assertEquals(expectedTable,actualTable);
        calif_estudiante[1]++;
    }

    private void validaNull_o_Vacio(String valor, String id, boolean nuevo) {
        String matricula=id;
        String nombre="Miguel";
        String apPaterno="Salas";
        String apMaterno="Guzman";
        String calle="Herreros 25";
        String colonia="Centro";
        String codpostal="99104";
        String telefono="4331234567";
        String email="misalas@uaz.edu.mx";
        long idest=32L;
        long idmun=32042L;

        boolean resultado=true;
        String valprev;

        Estudiante est = new Estudiante(id,valor,apPaterno,email);
        est.setApMaterno(apMaterno);
        est.setCalle(calle);
        est.setColonia(colonia);
        est.setCodPostal(codpostal);
        est.setTelefono(telefono);
        est.setIdEstado(idest);
        est.setIdMunicipio(idmun);

        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertFalse(resultado);

        est.setNombre(nombre);
        valprev=est.getApPaterno();
        est.setApPaterno(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertFalse(resultado);

        est.setApPaterno(valprev);
        valprev=est.getEmail();
        est.setEmail(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertFalse(resultado);

        est.setEmail(valprev);
        valprev=est.getApMaterno();
        est.setApMaterno(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertTrue(resultado);

        Connection conn;
        Statement stmt=null;
        String sql=String.format("DELETE FROM estudiante WHERE matricula='%s'",matricula);
        try {
            if (nuevo) {
                conn = conndbunit.getConnection();
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            }
        }
        catch (SQLException exsql) {
            assertNull("Problema al eliminar el estudiante que se agrego en la prueba",exsql);
        }


        est.setApMaterno(valprev);
        valprev=est.getCalle();
        est.setCalle(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertTrue(resultado);
        try {
            if (nuevo) {
                stmt.executeUpdate(sql);
            }
        }
        catch (SQLException exsql) {
            assertNull("Problema al eliminar el estudiante que se agrego en la prueba",exsql);
        }

        est.setCalle(valprev);
        valprev=est.getColonia();
        est.setColonia(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertTrue(resultado);
        try {
            if (nuevo) {
                stmt.executeUpdate(sql);
            }
        }
        catch (SQLException exsql) {
            assertNull("Problema al eliminar el estudiante que se agrego en la prueba",exsql);
        }

        est.setColonia(valprev);
        valprev=est.getCodPostal();
        est.setCodPostal(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertTrue(resultado);
        try {
            if (nuevo) {
                stmt.executeUpdate(sql);
            }
        }
        catch (SQLException exsql) {
            assertNull("Problema al eliminar el estudiante que se agrego en la prueba",exsql);
        }

        est.setCodPostal(valprev);
        est.setTelefono(valor);
        resultado=nuevo?dao.agregaEstudiante(est):dao.modificaEstudiante(est);
        assertTrue(resultado);
        try {
            if (nuevo) {
                stmt.executeUpdate(sql);
            }
        }
        catch (SQLException exsql) {
            assertNull("Problema al eliminar el estudiante que se agrego en la prueba",exsql);
        }
    }

    @Test
    @Order(4)
    public void testEstudianteAgregaInvalido() throws Exception {
        String[] matricula={"33081010","MATRICUL"};
        String[] nom={"Mario","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String[] apPat={"Ramirez","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String[] apMat={"Juarez","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String calle1="Carretera Zacatecas, Cd Cuauhtemoc km. 5";
        calle1 += calle1;
        calle1 += calle1;
        String[] calle={"Calle Tolosa 25",calle1};
        String colonia1="Ejido Cieneguitas";
        colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1;
        String[] colonia={"Centro",colonia1};
        String[] codpostal={"98000","5AG2B"};
        String[] telefono={"4921234567","49292762AA"};
        String[] email={"xxtat@yho.com","correoultralarguisimoquenodeberiadeseraceptado@masalla.delmasalla.delmasalla.com","noescorreo"};
        long[] idest={32,35};
        long[] idmun={32056,32100};


        Estudiante est = new Estudiante(matricula[1],nom[0],apPat[0],email[0]);
        est.setApMaterno(apMat[0]);
        est.setCalle(calle[0]);
        est.setColonia(colonia[0]);
        est.setCodPostal(codpostal[0]);
        est.setTelefono(telefono[0]);
        est.setIdEstado(idest[0]);
        est.setIdMunicipio(idmun[0]);

        boolean resultado;
        //resultado = true;
        try {
            validaNull_o_Vacio("",matricula[0],true);

            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setMatricula(matricula[0]);
            est.setNombre(nom[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setNombre(nom[0]);
            est.setApPaterno(apPat[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setApPaterno(apPat[0]);
            est.setApMaterno(apMat[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setApMaterno(apMat[0]);
            est.setCalle(calle[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setCalle(calle[0]);
            est.setColonia(colonia[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setColonia(colonia[0]);
            est.setCodPostal(codpostal[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setCodPostal(codpostal[0]);
            est.setTelefono(telefono[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setTelefono(telefono[0]);
            est.setEmail(email[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setEmail(email[2]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setEmail(email[0]);
            est.setIdEstado(idest[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

            est.setIdEstado(idest[0]);
            est.setIdMunicipio(idmun[1]);
            resultado=dao.agregaEstudiante(est);
            assertFalse(resultado);

        }
        catch (Exception ex) {
            assertNull("No deberia generar excepcion el metodo agregaEstudiante de DaoEscolaresREST",ex);
            System.err.println(ex.getMessage());
            resultado=true;
        }
        assertFalse(resultado);
        calif_estudiante[1]++;
    }

    @Test
    @Order(5)
    public void testEstudianteModificaValido() throws Exception {
        String matricula="31081514";
        String nombre="Luis";
        String apPaterno="Padilla";
        String apMaterno="Medina";
        String calle="Av. Torreon 901";
        String colonia="Las Lomas";
        String codpostal="99108";
        String telefono="4331234578";
        String email="lpadilla@uaz.edu.mx";
        long idest=1L;
        long idmun=1001L;

        Estudiante est = new Estudiante(matricula,nombre,apPaterno,email);
        est.setApMaterno(apMaterno);
        est.setCalle(calle);
        est.setColonia(colonia);
        est.setCodPostal(codpostal);
        est.setTelefono(telefono);
        est.setIdEstado(idest);
        est.setIdMunicipio(idmun);

        boolean resultado=false;
        try {
            resultado = dao.modificaEstudiante(est);
        }
        catch (Exception ex) {
            resultado=false;
            assertNull("No deberia generar excepcion el metodo modificaEstudiante de DaoEscolaresREST",ex);
        }
        assertTrue(resultado);

        ITable actualTable=conndbunit.createQueryTable("estudiante",
                String.format("SELECT * FROM estudiante WHERE matricula='%s'", matricula));

        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("estudiante_upd.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");

        Assertion.assertEquals(expectedTable,actualTable);
        calif_estudiante[2]++;
    }

    @Test
    @Order(6)
    public void testEstudianteModificaInexistente() throws Exception {
        String matricula="31081522";
        String nombre="Luis";
        String apPaterno="Padilla";
        String apMaterno="Medina";
        String calle="Av. Torreon 901";
        String colonia="Las Lomas";
        String codpostal="99108";
        String telefono="4331234578";
        String email="lpadilla@uaz.edu.mx";
        long idest=1L;
        long idmun=1001L;

        Estudiante est = new Estudiante(matricula,nombre,apPaterno,email);
        est.setApMaterno(apMaterno);
        est.setCalle(calle);
        est.setColonia(colonia);
        est.setCodPostal(codpostal);
        est.setTelefono(telefono);
        est.setIdEstado(idest);
        est.setIdMunicipio(idmun);

        boolean resultado=true;
        try {
            resultado = dao.modificaEstudiante(est);
        }
        catch (Exception ex) {
            assertNull("No deberia generar excepcion el metodo modificaEstudiante de DaoEscolaresREST",ex);
        }
        assertFalse(resultado);
        calif_estudiante[2]++;
    }

    @Test
    @Order(7)
    public void testEstudianteModificaInvalido() throws Exception {
        String[] matricula={"31081514","MATRICUL"};
        String[] nom={"Mario","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String[] apPat={"Ramirez","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String[] apMat={"Juarez","Nombre Extremadamente Largo para el Limite que tiene el campo y que por tanto no deberia de pasar Tecnologica Estado de Zacatecas"};
        String calle1="Carretera Zacatecas, Cd Cuauhtemoc km. 5";
        calle1 += calle1;
        calle1 += calle1;
        String[] calle={"Calle Tolosa 25",calle1};
        String colonia1="Ejido Cieneguitas";
        colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1; colonia1 += colonia1;
        String[] colonia={"Centro",colonia1};
        String[] codpostal={"98000","5AG2B"};
        String[] telefono={"4921234567","49292762AA"};
        String[] email={"xxtat@yho.com","correoultralarguisimoquenodeberiadeseraceptado@masalla.delmasalla.delmasalla.com", "noescorreo"};
        long[] idest={32,35};
        long[] idmun={32056,32100};


        Estudiante est = new Estudiante(matricula[1],nom[0],apPat[0],email[0]);
        est.setApMaterno(apMat[0]);
        est.setCalle(calle[0]);
        est.setColonia(colonia[0]);
        est.setCodPostal(codpostal[0]);
        est.setTelefono(telefono[0]);
        est.setIdEstado(idest[0]);
        est.setIdMunicipio(idmun[0]);

        boolean resultado;
        //resultado = true;
        try {
            validaNull_o_Vacio("",matricula[0],false);

            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setMatricula(matricula[0]);
            est.setNombre(nom[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setNombre(nom[0]);
            est.setApPaterno(apPat[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setApPaterno(apPat[0]);
            est.setApMaterno(apMat[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setApMaterno(apMat[0]);
            est.setCalle(calle[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setCalle(calle[0]);
            est.setColonia(colonia[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setColonia(colonia[0]);
            est.setCodPostal(codpostal[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setCodPostal(codpostal[0]);
            est.setTelefono(telefono[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setTelefono(telefono[0]);
            est.setEmail(email[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setEmail(email[2]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setEmail(email[0]);
            est.setIdEstado(idest[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

            est.setIdEstado(idest[0]);
            est.setIdMunicipio(idmun[1]);
            resultado=dao.modificaEstudiante(est);
            assertFalse(resultado);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            assertNull("No deberia generar excepcion el metodo modificaEstudiante de DaoEscolaresREST",ex);
            resultado=true;
        }

        assertFalse(resultado);
        calif_estudiante[2]++;
    }

    @Test
    @Order(8)
    public void testEstudianteEliminaValido() throws Exception {
        String id="31081514";
        try {
            boolean resultado=dao.eliminaEstudiante(id);
            assertTrue(resultado);
        }
        catch (Exception ex) {
            //System.err.println(ex.getMessage());
            ex.printStackTrace();
            assertNull("No deberia generar excepcion el metodo eliminaEstudiante de DaoEscolaresREST",ex);
        }

        //ITable actualTable=conndbunit.createTable("institucion");
        ITable actualTable=conndbunit.createTable("estudiante");

        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("estudiante.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");
        Assertion.assertEquals(expectedTable,actualTable);
        calif_estudiante[3]++;
    }


    @Test
    @Order(9)
    public void testEstudianteEliminaInexistente() throws Exception {
        String id="31081510";
        try {
            boolean resultado=dao.eliminaEstudiante(id);
            assertFalse(resultado);
        }
        catch (Exception ex) {
            assertNull("No deberia generar excepcion el metodo eliminaEstudiante de DaoEscolaresREST",ex);
        }
        calif_estudiante[3]++;
    }

    @Test
    @Order(10)
    public void testEstudianteEliminaInvalido() throws Exception {
        String id="29807050";
        try {
            boolean resultado=dao.eliminaEstudiante(id);
            assertFalse(resultado);
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            assertNull("No deberia generar excepcion el metodo eliminaEstudiante de DaoEscolaresREST",ex);
        }
        ITable actualTable=conndbunit.createTable("estudiante");

        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("estudiante.xml"));
        ITable expectedTable=expectedDataSet.getTable("estudiante");
        Assertion.assertEquals(expectedTable,actualTable);
        calif_estudiante[3]++;
    }


    /// MUNICIPIO

    private void comparaMunicipio(Municipio actual, ITable expected, int numrow) {
        try {
            assertEquals(String.valueOf(actual.getIdMunicipio()), expected.getValue(numrow, "id_municipio").toString());
            assertEquals(actual.getNombreMunicipio(), expected.getValue(numrow, "nombre_municipio"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            assertNull("No deberia generar excepcion comparar los municipios",ex);
        }
    }

    @Test
    @Order(11)
    public void testMunicipioObten() throws Exception {
        List<Municipio> actual;
        long id=32;
        try {
            actual=dao.obtenMunicipios(id);
        }
        catch (Exception ex) {
            actual=new ArrayList<>();
            ex.printStackTrace();
            assertNull("No deberia generar excepcion el metodo obtenMunicipios",ex);
        }
        IDataSet expectedDataSet=new FlatXmlDataSetBuilder().build(new File("municipio32.xml"));
        ITable expectedTable=expectedDataSet.getTable("municipio");

        assertEquals(actual.size(),expectedTable.getRowCount());
        for (int i=0; i<actual.size(); i++) {
            comparaMunicipio(actual.get(i), expectedTable,i);
        }

        id=1;
        try {
            actual=dao.obtenMunicipios(id);
        }
        catch (Exception ex) {
            actual=new ArrayList<>();
            ex.printStackTrace();
            assertNull("No deberia generar excepcion el metodo obtenMunicipios",ex);
        }
        expectedDataSet=new FlatXmlDataSetBuilder().build(new File("municipio1.xml"));
        expectedTable=expectedDataSet.getTable("municipio");

        assertEquals(actual.size(),expectedTable.getRowCount());
        for (int i=0; i<actual.size(); i++) {
            comparaMunicipio(actual.get(i), expectedTable,i);
        }
        calif_municipio += CALIF_MUNICIPIO;
    }

}

