import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class AliasImportHandler extends TypedHandlerDelegate {


    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        final Document document = editor.getDocument();
        AliasImportState instance = AliasImportState.getInstance();
        ArrayList<String> aliases = instance.aliases;
        Runnable runnable = () -> {
            if (c == '.') {
                if (!document.toString().contains("LightVirtualFile") || instance.pcveEnabled) {
                    Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
                    String[] strings = document.getText().split("\n");
                    String doc = document.getText().substring(0, primaryCaret.getOffset());
                    String[] lines = doc.split("\n");
                    String currentline = lines[lines.length - 1];
                    if (!currentline.contains("#")) {
                        for (String pair : aliases) {
                            String[] strs = pair.split(" ");
                            String k = strs[0];
                            String v = strs[1];
                            if (doc.endsWith(k + ".")) {
                                int non_comment_start_line = 0;
                                int place_to_insert = 0;
                                if (strings[0].startsWith("'''")) {
                                    place_to_insert = strings[0].length() + 1;
                                    for (int i = 1; i < strings.length; i++) {
                                        place_to_insert += strings[i].length() + 1;
                                        if (strings[i].endsWith("'''")) {
                                            non_comment_start_line = i + 1;
                                            break;
                                        }
                                    }
                                }
                                if (strings[0].startsWith("\"\"\"")) {
                                    place_to_insert = strings[0].length() + 1;
                                    for (int i = 1; i < strings.length; i++) {
                                        place_to_insert += strings[i].length() + 1;
                                        if (strings[i].endsWith("\"\"\"")) {
                                            non_comment_start_line = i + 1;
                                            break;
                                        }
                                    }
                                }
                                boolean flag = false;
                                for (int i = non_comment_start_line; i < strings.length; i++) {
                                    String s = strings[i];
                                    s = s.trim();
                                    System.out.println(s);
                                    if (s.equals("") || s.startsWith("#") || s.startsWith("from")) {
                                        continue;
                                    } else if (s.startsWith("import")) {
                                        if (s.startsWith("import " + v + " as " + k)) {
                                            flag = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (!flag) {
                                    String toinsert = "import " + v;
                                    if (!k.equals(v)) {
                                        toinsert = toinsert + " as " + k;
                                    }
                                    toinsert += "\n";
                                    document.insertString(place_to_insert, toinsert);
                                }
                            }
                        }
                    }
                }
            }
        };
        // Make the document change in the context of a write action.
        WriteCommandAction.runWriteCommandAction(project, runnable);

        return Result.STOP;
    }

}