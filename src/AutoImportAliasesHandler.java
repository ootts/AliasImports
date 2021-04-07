import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;

public class AutoImportAliasesHandler extends TypedHandlerDelegate {
    private HashMap<String, String> default_aliases = new HashMap<String, String>() {{
        put("np", "numpy");
        put("plt", "matplotlib.pyplot");
        put("osp", "os.path");
        put("pd", "pandas");
        put("tf", "tensorflow");
    }};
    private HashMap<String, String> aliases;

    public AutoImportAliasesHandler() {
        try {
            String usrHome = System.getProperty("user.home");
            String cfg_path = usrHome + "/.cache/map.json";
            boolean exists = checkExist(cfg_path);
            if (exists) {
                this.aliases = this.readjson(cfg_path);
            } else {
                this.aliases = default_aliases;
                this.writejson(default_aliases, cfg_path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        final Document document = editor.getDocument();
        Runnable runnable = () -> {
            if (!document.toString().contains("LightVirtualFile") && c == '.') {
                Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
                String[] strings = document.getText().split("\n");
                String doc = document.getText().substring(0, primaryCaret.getOffset());
                String[] lines = doc.split("\n");
                String currentline = lines[lines.length - 1];
                if (!currentline.contains("#")) {
                    this.aliases.forEach((k, v) -> {
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
//                            for (String s : strings) {
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
                                String toinsert = "import " + v + " as " + k + "\n";
                                document.insertString(place_to_insert, toinsert);
                            }
                        }
                    });
                }

            }
        };
        // Make the document change in the context of a write action.
        WriteCommandAction.runWriteCommandAction(project, runnable);

        return Result.STOP;
    }

    private boolean checkExist(String filepath) throws Exception {
        assert filepath.endsWith(".json");
        File file = new File(filepath);
        if (file.exists()) {//判断文件目录的存在
            return true;
        } else {
            File file2 = new File(file.getParent());
            file2.mkdirs();
            file.createNewFile();//创建文件
            return false;
        }
    }

    private HashMap<String, String> readjson(String filepath) {
        HashMap<String, String> result = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            JSONArray ja = new JSONArray(everything);
            JSONObject o = (JSONObject) ja.get(0);
            for (int i = 0; i < o.length(); i++) {
                String k = (String) o.keySet().toArray()[i];
                String v = (String) o.get(k);
                result.put(k, v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void writejson(HashMap<String, String> map, String filepath) {
        JSONArray array = new JSONArray();
        array.put(map);
        try (FileWriter file = new FileWriter(filepath)) {
            file.write(array.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}