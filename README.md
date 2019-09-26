# beers-api
BEERS API - Spring Boot

Esta aplicación está construida con la finalidad de componer una
demostración sencilla, pero con la intención de ser lo más cercana 
a una implementación comúnmente utilizada en el marco empresarial, 
de una API REST utilizando Spring Boot JPA y WEB (@RestController).


Por motivos de tiempo y para hacer el codigo más sencillo, evité 
encapsular las respuestas en objetos ResponseEntity personalizados,
lo que hubiese permitido, por ejemplo, personalizar la descripción de
la respuestas de la api, pero en contraparte, se genera cierta redundancia 
en algunas de estas respuesta que ya están manejadas por el mismo
framework (Javax Validations, Spring Validations), o por las convensiones
de REST. De todas formas, esto es algo que se podria agregar por supuesto.


Para ejecutar la aplicación, descargar o clonar repositorio
y en el path principal (donde se encuentra el archivo pom.xml y docker-compose.yml)
ejecutar:

En shell unix (es necesario tener instalado Docker): 
 `docker-compose up --build`

 
 Una vez que la aplicación está ejecutandose, se puede acceder a la
 documentación de la API, la cual está descrita mediante Open API (Swagger):
 http://localhost:8080/swagger-ui.html
 
 
 
 
 
 

 
# beers-api
