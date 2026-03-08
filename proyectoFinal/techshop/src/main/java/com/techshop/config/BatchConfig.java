package com.techshop.config;

import com.techshop.batch.ProductoItemProcessor;
import com.techshop.modelo.Producto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Configuracion de Spring Batch para la carga masiva de productos desde CSV.
 * Define un job con un solo step que lee el archivo productos.csv del classpath,
 * valida cada registro con el processor y escribe los validos en MongoDB.
 * El job NO se auto-ejecuta al iniciar la app (spring.batch.job.enabled=false).
 */
@Configuration
public class BatchConfig {

    /**
     * Reader que lee el archivo CSV de productos desde el classpath.
     * Usa DelimitedLineTokenizer con comillas para manejar el JSON embebido en la columna atributos.
     * Mapea cada linea a un objeto Producto usando un FieldSetMapper personalizado.
     */
    @Bean
    public FlatFileItemReader<Producto> productoItemReader() {
        return new FlatFileItemReaderBuilder<Producto>()
                .name("productoItemReader")
                .resource(new ClassPathResource("productos.csv"))
                .delimited()
                .quoteCharacter('"')
                .names("nombre", "descripcion", "precio", "stock", "imagenUrl", "categoria", "atributos")
                .fieldSetMapper(productoFieldSetMapper())
                .linesToSkip(1) // saltar encabezado
                .build();
    }

    /**
     * FieldSetMapper personalizado que convierte cada linea del CSV a un Producto.
     * El campo atributos viene como JSON string; se almacena temporalmente con la clave "_json"
     * para que el ProductoItemProcessor lo parsee a Map.
     */
    @Bean
    public FieldSetMapper<Producto> productoFieldSetMapper() {
        return fieldSet -> {
            Producto producto = new Producto();
            producto.setNombre(fieldSet.readString("nombre"));
            producto.setDescripcion(fieldSet.readString("descripcion"));
            producto.setPrecio(new BigDecimal(fieldSet.readString("precio")));
            producto.setStock(fieldSet.readInt("stock"));
            producto.setImagenUrl(fieldSet.readString("imagenUrl"));
            producto.setCategoria(fieldSet.readString("categoria"));

            // Guardar JSON crudo en un Map temporal para que el Processor lo parsee
            String atributosJson = fieldSet.readString("atributos");
            if (atributosJson != null && !atributosJson.isBlank()) {
                producto.setAtributos(Map.of("_json", atributosJson));
            }

            return producto;
        };
    }

    /**
     * Writer que persiste los productos validados en la coleccion "productos" de MongoDB.
     */
    @Bean
    public MongoItemWriter<Producto> productoItemWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<Producto>()
                .template(mongoTemplate)
                .collection("productos")
                .build();
    }

    /**
     * Step principal: lee CSV -> valida/transforma -> escribe en MongoDB.
     * Usa chunks de 10 elementos para balance entre rendimiento y uso de memoria.
     */
    @Bean
    public Step importarProductosStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      FlatFileItemReader<Producto> productoItemReader,
                                      ProductoItemProcessor productoItemProcessor,
                                      MongoItemWriter<Producto> productoItemWriter) {
        return new StepBuilder("importarProductosStep", jobRepository)
                .<Producto, Producto>chunk(10, transactionManager)
                .reader(productoItemReader)
                .processor(productoItemProcessor)
                .writer(productoItemWriter)
                .build();
    }

    /**
     * Job de importacion masiva de productos.
     * Se ejecuta bajo demanda via el endpoint REST POST /api/batch/cargar-productos.
     */
    @Bean
    public Job importarProductosJob(JobRepository jobRepository, Step importarProductosStep) {
        return new JobBuilder("importarProductosJob", jobRepository)
                .start(importarProductosStep)
                .build();
    }
}
