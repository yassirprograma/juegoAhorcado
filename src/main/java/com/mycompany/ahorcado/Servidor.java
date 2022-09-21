package com.mycompany.ahorcado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.*;

/*TAREA 1
AUTORES:
FUENTES GARCÍA KEVIN YASSIR
GUERRERO ZORZA ERICK EDMUNDO
*/

//EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
 
public class Servidor {      
      
      public static void main(String[] args) {
        try{
            int pto =8000;
            
            ServerSocket servidor = new ServerSocket(pto);
            System.out.println("El servidor del videojuego AHORCADO ha sido iniciado"+pto+" .. esperando cliente.."); //MENSAJE DE INICIO DEL SERVER
                        
            
            while(true){ //Este ciclo es infinito (para esperar a un cliente)
                /*A pesar de tener un ciclo infinito, las instrucciones de un socket bloqueante
                detienen el flujo del programa (se queda en espera) hasta obtener todo lo necesario para poder llevarla a cabo
                Por esa razón, el ciclo no da vueltas todo el tiempo, sino que las instrucciones que están dentro, se quedan esperando hasta poder llevarse a cabo
                */
                
                //Esperamos a que un cliente solicite conexión al servidor 
                Socket cliente = servidor.accept(); //Servidor espera hasta aceptar una conexión, se define como socket en cliente (socket servidor en espera)
                
                //Imprimimos que se ha aceptado la conexión
                System.out.println("Jugador conectado desde "+cliente.getInetAddress()+":"+cliente.getPort()); 
                
                //Establecemos buffers para lectura y escritura  
                PrintWriter escritor = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream(),"ISO-8859-1"));//Buffer para escritura sobre el socket del cliente
                BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream(),"ISO-8859-1"));//Buffer para lectura del socket del cliente
                
                
                String mensajeParaCliente=" "; //String para los mensajes que le iremos mandando al cliente
                
                //Mandamos la indicación al cliente de que ingrese un nombre de usuario
                escritor.flush();
                mensajeParaCliente="Ingrese un nombre de usuario: ";
                escritor.println(mensajeParaCliente);
                escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
                
                
                String nombreUsuario=lector.readLine(); //Esperamos a leer el nombre de usuario del cliente que se ha conectado
                System.out.println("El cliente ha elegido el nombre de usuario: "+nombreUsuario);

                
                //Mandamos la indicación al cliente de que elija un nivel de juegp
                escritor.flush();
                mensajeParaCliente="Elija un nivel del juego <1,2 o 3> :";
                escritor.println(mensajeParaCliente);
                escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
                
                int nivelJuego=parseInt(lector.readLine()); //Esperamos a leer el nivel que ha elegido el cliente
                System.out.println("El cliente ha elegido jugar en nivel "+nivelJuego);
                
                
                //Una vez conocido el nivel, generamos una cadena de acuerdo a ese nivel
                cadenasJuego cadenaGenerada = new cadenasJuego(nivelJuego);
                
                System.out.println(cadenaGenerada.cadenaOriginal); //imprimimos la cadena generada original
                System.out.println(cadenaGenerada.cadenaIncompleta+"\n"); //imprimos la cadena generada incompleta (esta se mandará al cliente para que la vaya completando)
                
                
                //mandamos al cliente la cadena INCOMPLETA generada
                mensajeParaCliente=cadenaGenerada.cadenaIncompleta;
                escritor.flush();                
                escritor.println(mensajeParaCliente);
                escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
                
                //mandamos al cliente la cantidad de vidas                                
                //solo son 5 intentos                                                  
                
                int vidas=5;                
                //mandamos al cliente la cantidad de vidas disponibles
                String v=Integer.toString(vidas);
                System.out.println(v);
                escritor.flush();                
                escritor.println(v);
                escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere                
                
                String intento; //cadena para ir guardando
                String banderaCadenaOk="0"; //bandera para indicarle al cliente que se ha logrado completar la cadena
                        
                while(vidas>0){ //mantenemos un ciclo con el cliente, mientras no se acaben las vidas                
                    //EL SERVIDOR ES EL QUE LLEVA EL CONTEO
                    v=Integer.toString(vidas);
                    System.out.println(v);
                    escritor.flush();                
                    escritor.println(v);
                    escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere                
                
                    
                    //recibimos el intento del cliente
                    intento=lector.readLine();
                    System.out.println("Intento recibido:"+ intento);
                    
                    
                    //el servidor procesa y completa                                        
                    //devuelve la cadena luego de agregarse el caracter seleccionado por el cliente o en su caso                    
                    cadenaGenerada.cadenaIncompleta=completaCadena(cadenaGenerada.cadenaIncompleta, cadenaGenerada.cadenaOriginal, intento);
                                        
                    
                    
                    System.out.println(cadenaGenerada.cadenaIncompleta);
                    
                    //mandamos al cliente la cadena procesada
                    mensajeParaCliente=cadenaGenerada.cadenaIncompleta;
                    escritor.flush();                
                    escritor.println(mensajeParaCliente);
                    escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
                    
                    
                    //si adivina todo (hacer la comparación aquí), cortamos el ciclo 
                    if(cadenaGenerada.cadenaIncompleta.equals(cadenaGenerada.cadenaOriginal))
                    {                        
                        banderaCadenaOk="1"; 
                    }
                                        
                    escritor.flush();                
                    escritor.println(banderaCadenaOk);//mandamos al cliente la bandera
                    escritor.flush(); //EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere
                    
                    if(banderaCadenaOk.equals("1"))break; //rompemos el ciclo                                                               
                    
                    vidas--;//bajamos una vida por intento
                }
                
                //System.out.println("Presione ENTER para finalizar el juego");
                //System.in.read();
                //break;
                
            }
            
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static String completaCadena(String cadenaIncompleta,String cadenaOriginal, String intento){
          
          if(intento.length()>1){
             //si el intento es una palabra/frase
             if(intento.equals(cadenaOriginal))//comparamos el intento con la cadena original
                cadenaIncompleta=cadenaOriginal; // solo si el intento coincide con la original 
              //sino, la cadena incompleta, se queda igual
          }else{
              //si el intento es una letra
              //recorremos la cadena original en busca de las posiciones donde esté esa letra
              for(int i=0;i<cadenaOriginal.length();i++){
                  
                  if(cadenaOriginal.charAt(i)==intento.charAt(0)){
                      
                      // convierte la string dada en una array de caracteres
                        char[] chars = cadenaIncompleta.toCharArray();
 
                       // reemplaza el carácter en la posición especificada en una array de caracteres
                        chars[i] = intento.charAt(0);
 
                        // convierte la array de caracteres de nuevo en una string
                        cadenaIncompleta = String.valueOf(chars); 
                  }
              }
          }
          
          return cadenaIncompleta;
      }
      
     
}


 
class cadenasJuego{
            String cadenaOriginal;
            String cadenaIncompleta;
                       
            public cadenasJuego (){ //constructor base
                cadenaOriginal=" ";
                cadenaIncompleta=" ";
            }
            
            int numeroAleatorio(int min, int max){            
                Random random = new Random();
                int value = random.nextInt(max + min) + min;            
                return value;
            }
            public cadenasJuego(int nivel){ //Esta función devuelve una cadena aleatoria según el nivel elegido
            
            //6 cadenas por nivel
            String [] cadenasOriginalesNivel1=new String[6]; //Palabras cortas (menor o igual a 5 letras)
            String [] cadenasIncompletasNivel1=new String[6]; //Incompletas
            
            String [] cadenasOriginalesNivel2=new String[6]; //Palabras de más de 5 letras
            String [] cadenasIncompletasNivel2=new String[6]; //Incompletas
            
            String [] cadenasOriginalesNivel3=new String[6]; //Frases u oraciones (a estas se les deben eliminar )
            String [] cadenasIncompletasNivel3=new String[6]; //incompletas
            
            //Definimos las cadenas  para cada nivel (6 GADENAS POR NIVEL)            
            
            //PARA EL NIVEL 1 (menor o igual a 5 letras, de 2 o 3 omisiones)
            cadenasOriginalesNivel1[0]="hola"; cadenasIncompletasNivel1[0]="h_l_";  cadenasOriginalesNivel1[1]="perro"; cadenasIncompletasNivel1[1]="_er_o"; 
            cadenasOriginalesNivel1[2]="amor"; cadenasIncompletasNivel1[2]="_m_r";  cadenasOriginalesNivel1[3]="árbol"; cadenasIncompletasNivel1[3]="á_b_l"; 
            cadenasOriginalesNivel1[4]="pollo"; cadenasIncompletasNivel1[4]="_o_lo";  cadenasOriginalesNivel1[5]="amigo"; cadenasIncompletasNivel1[5]="a__g_"; 
            
            //PARA EL NIVEL 2 (más de 5 letras, de 4 a 8 omisiones)
            cadenasOriginalesNivel2[0]="ferrocarril"; cadenasIncompletasNivel2[0]="f__r_c_r__l"; cadenasOriginalesNivel2[1]="celular"; cadenasIncompletasNivel2[1]="c__u__r";
            cadenasOriginalesNivel2[2]="saltamontes"; cadenasIncompletasNivel2[2]="s_l__m__t__"; cadenasOriginalesNivel2[3]="aeropuerto"; cadenasIncompletasNivel2[3]="a__o__e_t_";
            cadenasOriginalesNivel2[4]="retroexcavadora"; cadenasIncompletasNivel2[4]="r_t__e__a__d__a"; cadenasOriginalesNivel2[5]="aspiradora"; cadenasIncompletasNivel2[1]="a_p_r_d__a";
            
            //PARA EL NIVEL 3 (de 5 a más omisiones)
            cadenasOriginalesNivel3[0]="Anita lava la tina"; cadenasIncompletasNivel3[0]="A__t_ l__a l_ t__a";
            cadenasOriginalesNivel3[1]="Al infinito y más allá"; cadenasIncompletasNivel3[1]="A_ i_f__it_ y _á_ a__á";
            cadenasOriginalesNivel3[2]="Yo soy tu padre"; cadenasIncompletasNivel3[2]="Y_ s__ t_ p_d_e";
            cadenasOriginalesNivel3[3]="huelum huelum gloria"; cadenasIncompletasNivel3[3]="h_él__ _ué_u_ g_o_i_";
            cadenasOriginalesNivel3[4]="Pero sigo siendo el rey"; cadenasIncompletasNivel3[4]="P_r_ s_g_ s__nd_ _l r_y";
            cadenasOriginalesNivel3[5]="Si no supiste amar, ahora te puedes marchar"; cadenasIncompletasNivel3[5]="Si n_ s_p_st_ a_ar, a_ora t_ p__de_ m_r_har";            //genero un número aleatorio, para elegir al azar una cadena en un nivel
           
            
            int eleccionAleatoria= numeroAleatorio(0,5); //ese número será la posición del diccionario del nivel seleccionado
                            
                switch (nivel){
                    case 1:{ //devolvemos una cadena elegida aleatoriamente del nivel1       
                        System.out.println("Nivel 1");
                        this.cadenaOriginal=cadenasOriginalesNivel1[eleccionAleatoria];
                        this.cadenaIncompleta=cadenasIncompletasNivel1[eleccionAleatoria];                        
                        break;
                    }
                                            
                    case 2:{//devolvemos una cadena elegida aleatoriamente del nivel2                       
                        System.out.println("Nivel 2");
                        this.cadenaOriginal=cadenasOriginalesNivel2[eleccionAleatoria];
                        this.cadenaIncompleta=cadenasIncompletasNivel2[eleccionAleatoria];                        
                        break;
                    }                        

                    case 3:{
                        System.out.println("Nivel 3");
                        //devolvemos una cadena elegida aleatoriamente del nivel1                        
                        this.cadenaOriginal=cadenasOriginalesNivel3[eleccionAleatoria];
                        this.cadenaIncompleta=cadenasIncompletasNivel3[eleccionAleatoria];                        
                        break;
                    }
                        
                    default:
                        //return null;
                }       
                                                
            }   
            
            
        }
