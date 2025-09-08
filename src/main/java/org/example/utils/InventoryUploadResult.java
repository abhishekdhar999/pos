package org.example.utils;

import lombok.Getter;
import lombok.Setter;
import org.example.models.form.InventoryForm;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class InventoryUploadResult {
    private List<InventoryForm> inventories = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
}
