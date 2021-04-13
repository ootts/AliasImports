import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


@State(
        name = "org.intellij.settings.AliasImportState",
        storages = {@Storage("AliasImportPlugin.xml")}
)
public class AliasImportState implements PersistentStateComponent<AliasImportState> {

    public boolean pcEnabled = false;
    public boolean veEnabled = false;
    public ArrayList<String> aliases = new ArrayList<>() {{
        add("np numpy");
        add("plt matplotlib.pyplot");
        add("osp os.path");
        add("pd pandas");
        add("tf tensorflow");
        add("torch torch");
    }};

    public static AliasImportState getInstance() {
        return ServiceManager.getService(AliasImportState.class);
    }

    @Nullable
    @Override
    public AliasImportState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AliasImportState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
