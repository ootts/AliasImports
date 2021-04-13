// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AliasImportConfigurable implements Configurable {

    private AliasImportComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AliasImports";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AliasImportComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AliasImportState settings = AliasImportState.getInstance();
        boolean modified = mySettingsComponent.getPCEnabled() != settings.pcEnabled;
        modified |= mySettingsComponent.getVEEnabled()!=settings.veEnabled;
        modified |= !mySettingsComponent.getTable().toString().equals(settings.aliases.toString());
        return modified;
    }

    @Override
    public void apply() {
        AliasImportState settings = AliasImportState.getInstance();
        settings.pcEnabled = mySettingsComponent.getPCEnabled();
        settings.veEnabled = mySettingsComponent.getVEEnabled();
        settings.aliases = mySettingsComponent.getTable();
    }

    @Override
    public void reset() {
        AliasImportState settings = AliasImportState.getInstance();
        mySettingsComponent.setPCEnabled(settings.pcEnabled);
        mySettingsComponent.setVEEnabled(settings.veEnabled);
        mySettingsComponent.setTable(settings.aliases);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
