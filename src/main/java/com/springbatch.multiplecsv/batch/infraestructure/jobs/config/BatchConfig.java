package com.springbatch.multiplecsv.batch.infraestructure.jobs.config;

import com.springbatch.multiplecsv.batch.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("classpath*:/data/inputData*.csv")
    private Resource[] inputResources;

    //para sacar la informacion en archivo

    private Resource outPutResource = new FileSystemResource("output/outputData.csv");

    @Bean
    public Job readCSVFilesJob(){
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();

    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<Employee, Employee>chunk(5)
                .reader(multiResourceItemReader())
                .writer(writer())
                .build();
    }
    @Bean
    public MultiResourceItemReader<Employee> multiResourceItemReader(){
        MultiResourceItemReader<Employee> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FlatFileItemReader<Employee> reader(){
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<>(){
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames(new String[]{"id","firstName","lastName"});
                    }
                });
                //set values  in Employee Class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>(){
                    {
                        setTargetType(Employee.class);
                    }
                });
            }
        });
        return reader;
    }

//    @Bean
//    public ConsoleItemWriter<Employee> writer(){
//        return new ConsoleItemWriter<Employee>();
//    }
      @Bean
      public ConsoleItemWriter writer(){
            return new ConsoleItemWriter();
      }
      @Bean
      public FlatFileItemWriter<Employee> writerFlatPlane(){

        //CREATE WRITER INSTANCE
          FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();
          //setup output file location

          writer.setResource(outPutResource);
          //All job repetitions should "append" to same output file
          writer.setAppendAllowed(true);
          //Name field values sequence based on object properties
          writer.setLineAggregator(new DelimitedLineAggregator<Employee>(){
              {
                  setDelimiter(",");
                  setFieldExtractor(new BeanWrapperFieldExtractor<Employee>(){
                      {
                          setNames(new String [] {"id","firstName","lastName"});
                      }
                  });
              }
          });
          return writer;

      }
      @Bean
      public Step step2(){
        return stepBuilderFactory.get("step2")
                .<Employee,Employee>chunk(5)
                .reader(multiResourceItemReader())
                .writer(writerFlatPlane())
                .build();
    }
}
