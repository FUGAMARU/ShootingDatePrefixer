package com.fugamaru.sdp.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class ExecutionArgumentsBean {
    @Option(name = "-mod", usage = "Use the file modification date as a prefix if the shooting date could not be obtained from the metadata.")
    private boolean modFlag;
    @Argument
    private ArrayList<String> arguments = new ArrayList<>();
}
