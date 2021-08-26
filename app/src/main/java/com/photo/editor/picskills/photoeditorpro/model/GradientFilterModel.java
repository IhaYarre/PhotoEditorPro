package com.photo.editor.picskills.photoeditorpro.model;

import java.util.Arrays;
import java.util.List;

public class GradientFilterModel {
    private String gradientFilterName;

    public GradientFilterModel(String name) {
        gradientFilterName = name;
    }

    public String getGradientFilterName() {
        return gradientFilterName;
    }

    public void setGradientFilterName(String gradientFilterName) {
        this.gradientFilterName = gradientFilterName;
    }

    public static List<GradientFilterModel> gradientList = Arrays.asList(
            new GradientFilterModel("None"),
            new GradientFilterModel("G1"),
            new GradientFilterModel("G2"),
            new GradientFilterModel("G3"),
            new GradientFilterModel("G4"),
            new GradientFilterModel("G5"),
            new GradientFilterModel("G6"),
            new GradientFilterModel("G7"),
            new GradientFilterModel("G8"),
            new GradientFilterModel("G9"),
            new GradientFilterModel("G10"),
            new GradientFilterModel("G11"),
            new GradientFilterModel("G12"),
            new GradientFilterModel("G13"),
            new GradientFilterModel("G14"),
            new GradientFilterModel("G15"),
            new GradientFilterModel("G16"),
            new GradientFilterModel("G17"),
            new GradientFilterModel("G18"),
            new GradientFilterModel("G19"),
            new GradientFilterModel("G20"),
            new GradientFilterModel("G21")
    );
}
