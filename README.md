# P2-Indices

## Indexación y hashing

Considere la aplicación bancaria de la práctica I, la cual administra registros con los siguientes campos: SUCURSAL (cadena de 20 bytes), NUMERO (entero de 4 bytes), NOMBRE (cadena de 20 bytes), SALDO (real de 6 bytes). Para indexar dichos registros usando un índice primario, se pondrá a su disposición un programa que implementa el siguiente esquema conceptual:

![Diagrama de clases](http://s28.postimg.org/ujtyuzukd/diagrama_de_clases.png)

Haciendo uso de este programa, realice las siguientes actividades:
  1.	Extender el programa para eliminar y recuperar registros: uno o varios. Use un byte adicional para marcar los registros de datos y compacte el archivo del índice cada vez que una entrada deba ser eliminada.
    a.	Definir al menos dos métodos que permitan realizar búsquedas lineales: Una para encontrar un registro y otra para encontrar un grupo.
    b.	Insertar ahora al menos 100,000 registros generados de manera aleatoria. Asegurar que los valores dentro de cada campo tengan una distribución uniforme.
    c.	Definir un conjunto de pruebas significativas para su demostración: Inserciones, eliminaciones y búsquedas. Tome tiempos para comparar sus resultados más adelante.

  2.	Construir ahora un nuevo programa que permita insertar, eliminar y recuperar registros utilizando un índice disperso. Para ello suponga que cada 10 registros tenemos el inicio de un nuevo bloque.
    a.	Definir al menos dos métodos que permitan realizar búsquedas lineales: Una para encontrar un registro y otra para encontrar un grupo.
    b.	Insertar al menos 100,000 registros generados de manera aleatoria. Asegurar que los valores dentro de cada campo tengan  una distribución uniforme.
    c.	Definir un conjunto de pruebas significativas para su demostración: Inserciones, eliminaciones y búsquedas. Tome tiempos para comparar sus resultados más adelante.

  3.	Construir una tabla que permita comparar los tiempos obtenidos después de ejecutar inserciones, eliminaciones y búsquedas en ambos índices:
    a.	Discuta bajo qué condiciones es mejor usar el índice denso.
    b.	De manera similar discuta las condiciones bajo las cuáles es más interesante usar el índice disperso.
