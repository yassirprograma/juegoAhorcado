package com.mycompany.ahorcado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.Integer.parseInt;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

//EUREKAAA: siempre siempre que se escriba algo, debe limpiarse el BUFFER para que el socket que está bloqueado se libere

public class Cliente {
    public static void main(String[] args) {
        try{
            int pto = 8000;
            BufferedReader entradaTeclado = new BufferedReader(new InputStreamReader(System.in,"ISO-8859-1"));//CONJUNTO DE CARACTERES QUE ACEPTA EL ESPAÑOL LATINO (buffer de teclado)
            InetAddress host = null; //PARA GUARDAR LA DIRECCIÓN 1P
            String dir=""; //CADENA PARA LEER LA IP
            
            try{
                
                System.out.println("Escribe la direccion del servidor :"); 
                dir = entradaTeclado.readLine();  //Leemos COMO CADENA la dirección del server (DESDE TECLADO)
                host = InetAddress.getByName(dir); //Pasa de CADENA al formato de dirección IP
            }catch(Exception n){
                System.out.println("Error al encontrar la IP, vuelva a ingresar la IP");                
               main(args); 
            }

            Socket cl = new Socket(host,pto); //Establecemos conexión con el servidor mediante el socket (INSTANCIAMOS EL SOCKET CLIENTE)            
            System.out.println("Conexion establecida con el servidor del juego\n"); //IMPRIMIMOS QUE SE HA CONECTADO CON EL SERVER
            
            
            //CREAMOS BUFFERS PARA LECTURA Y ESCRITURA DEL SOCKET
            PrintWriter escritor = new PrintWriter(new OutputStreamWriter(cl.getOutputStream(),"ISO-8859-1")); //BUFFER DE ESCRITURA QUE ACEPTA EL CONJUNTO DE CARACTERES PARA ESPAÑOL LATINO
            BufferedReader lector = new BufferedReader(new InputStreamReader(cl.getInputStream(),"ISO-8859-1")); //BUFFER DE LECTURA QUE ACEPTA EL CONJUNTO DE CARACTERES PARA ESPAÑOL LATINO
            
            
            
            String mensajeDelServer=" ";//cadena para guardar los mensajes que el servidor manda al cliente
            
            System.out.println("¡Bienvenido al juego del Ahorcado!");
            
            
            mensajeDelServer=lector.readLine();                        
            System.out.println("El server dice: "+ mensajeDelServer);//imprimimos el mensaje que manda el server (el server pide el username)
                        
            ///////Elegimos el nombre usuario y se lo mandamos al servidor                                    ;
            escritor.flush(); //siempre limpiar el buffer antes de enviar algo            
            String nombreUsuario=entradaTeclado.readLine();
            escritor.println(nombreUsuario);                                                
            escritor.flush();
            /////////////////////
            
            System.out.println("\nHola "+nombreUsuario+"\n\nPresione ENTER para empezar a jugar al AHORCADO");
            entradaTeclado.readLine();
            
            
            mensajeDelServer=lector.readLine(); //recibimos el siguiente mensaje del servido                        
            System.out.println("El server dice: "+ mensajeDelServer);//imprimimos el mensaje que manda el server (el server pide el niveldelJuego)
                        
            ///////mandamos al server el nivel elegidor
            escritor.flush(); //siempre limpiar el buffer antes de enviar algo            
            String nivelJuego=" ";
            while(!nivelJuego.equals("1")&& !nivelJuego.equals("2") && !nivelJuego.equals("3")){
                if(!nivelJuego.equals(" ")) 
                    System.out.println("Ingrese un valor válido");
                
                nivelJuego=entradaTeclado.readLine();  
                System.out.println(nivelJuego);
            }
            
            escritor.println(nivelJuego);                                                
            escritor.flush();
            /////////////////////
            
            
            //el server manda la cadena incompleta
            String cadenaGenerada=lector.readLine(); //recibimos el siguiente mensaje del servido                                    
            System.out.println("Intenta completar la palabra\n\n"+ cadenaGenerada+"\n");//imprimimos el mensaje que manda el server (el server manda la cadena incompleta)
            
            
            //el server manda la cantidad de vidas
            String cadenaVidas=lector.readLine(); //recibimos del servidor la cantidad de vidas 
            int vidas=parseInt(cadenaVidas);//convertimos
            
            String intento=new String(); //
            
            //Creamos un arreglo para ir guardando las letras que ya se han ingresado
            Vector intentosAnteriores=new Vector(0, 1);
            String banderaOk="0"; //bandera para que el server nos indique si se ha logrado o no el reto
            
            while(vidas-1>0){//mantenemos un ciclo con el servidor, mientras no se acaben las vidas
                //-1, pues el valor de vidas todavía debe actualizarse
                //el cliente tiene opción de elegir letra por letra o escribir la frase/palabra completa
                
                
                cadenaVidas=lector.readLine(); //recibimos el conteo que lleva el servidor (actualizamos desde el servidor)
                vidas=parseInt(cadenaVidas); 
                System.out.println("\nVidas restantes: "+ vidas);//imprimimos el mensaje que manda el server (el server manda la cantidad de vidas)            
                
                if(intentosAnteriores.size()>0){ //le recordamos al cliente las letras que ya ha usado
                    System.out.print("Intentos anteriores: ");
                    for(int i=0;i<intentosAnteriores.size();i++){                        
                        System.out.print(intentosAnteriores.elementAt(i)+" ");
                    }
                    System.out.println(" ");
                }
                
                System.out.println("\nEscriba la letra que desea usar para completar el acertijo, o en caso de adivinar completamente la frase/palabra, ingrésela: ");
                
                
                //recibimos la letra/cadena que se ingresa desde teclado
                intento=entradaTeclado.readLine();
                //guardamos en el arreglo de letrasYaUsadas 
                intentosAnteriores.addElement(intento);
                
                //le enviamos al server el intento de completar
                escritor.flush(); //siempre limpiar el buffer antes de enviar algo                            
                escritor.println(intento);                                                
                escritor.flush();
                
                //Esperamos a que el servidor procese y agregue el caracter a la cadena incompleta                
                //recibimos la cadena que el servidor ha procesado y complementado                
                
                //el server manda la cadena incompleta
                cadenaGenerada=lector.readLine(); //actualizamos la cadena
                System.out.println("Reto a vencer: "+ cadenaGenerada);                               
                
                
                
               //si adivina todocortamos el ciclo
               //esperamos del server la bandera que indica si se ha completado o no el reto
               banderaOk=lector.readLine(); //actualizamos la cadena
               
               
               if(banderaOk.equals("1")){
                   System.out.println(banderaOk);
                  break;
               }
               
            }
            
            
            //si la cadena modificada es igual a la original, entonces, se logró sino, se perdió
            if(banderaOk.equals("1")){
                System.out.println("Has completado correctamente la palabra/frase: "+ cadenaGenerada);
                System.out.println("¡FELICIDADES!");
            }else {
                System.out.println("Tus intentos se acabaron y no lograste vencer el reto (el muñequito se ahorcó), intenta de nuevo");
            }
                                    
            
                
            System.out.println("Presione cualquier letra para finalizar el juego");
            System.in.read();
            
            escritor.close();
            lector.close();
            cl.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

