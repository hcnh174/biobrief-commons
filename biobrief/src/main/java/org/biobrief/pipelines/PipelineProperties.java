package org.biobrief.pipelines;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated @NotNull
@ConfigurationProperties("pipelines")
public class PipelineProperties
{
	@Valid @NotNull private String outDir;//=SlurmConstants.DEFAUT_SLURM_OUT_DIR;

//	public String getOutDir(){return this.outDir;}
//	public void setOutDir(final String outDir){this.outDir=outDir;}
}
