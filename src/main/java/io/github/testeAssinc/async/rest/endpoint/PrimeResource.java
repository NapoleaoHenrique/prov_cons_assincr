
package io.github.testeAssinc.async.rest.endpoint;

import io.github.testeAssinc.async.rest.util.PrimeManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;


@Path("prime")
public class PrimeResource {

    //Injetando criacao de numeros primos
    @EJB
    private PrimeManager primeManager;

    @GET
    @Path("{decimalPlaces}")
    public void getRandomPrimeNumberWithSize(@Suspended AsyncResponse asyncResponse, @PathParam(value = "decimalPlaces") int decimalPlaces) throws NamingException {
        //lookup fábrica de threads gerenciadas
        ManagedThreadFactory threadFactory = (ManagedThreadFactory) new InitialContext()
                .lookup("java:comp/DefaultManagedThreadFactory");

        //Gerenciador de tarefas para processar regras de negócio
        ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);

        //Adicionando a tarefa a ser executada para esta requisição 
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //Gera número primo do tamanho pedido
                int myPrimeNumber = primeManager.generatePrimeNumber(decimalPlaces);
                //Retorna resultado cliente
                asyncResponse.resume(myPrimeNumber);
            }
        });

        //tratamento timeout
        asyncResponse.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse ar) {
                //para tentativa de encontrar número primo
                executorService.shutdownNow();
                //responde para o cliente informando para tentar um número menor
                Response response = Response.status(Response.Status.SERVICE_UNAVAILABLE).
                        header("Error", "Operation timed out, try to get a smaller prime number").build();
                //resposta para o cliente
                ar.resume(response);
            }
        });
        asyncResponse.setTimeout(4000, TimeUnit.MILLISECONDS);
    }

}
