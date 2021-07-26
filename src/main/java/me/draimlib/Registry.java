package me.draimlib;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Registry {
    private final Map<Class<? extends Module>, Module> moduleMap = Maps.newLinkedHashMap();
    private final DraimLib plugin;
    private final ClassLoader classLoader;

    public Registry(DraimLib plugin, ClassLoader classLoader) {
        this.plugin = plugin;
        this.classLoader = classLoader;
    }

    public void registerModules(String path) {
        List<Class<? extends Module>> classes = Lists.newArrayList();
        try {
            ClassPath classPath = ClassPath.from(classLoader);
            for(ClassInfo info : classPath.getTopLevelClassesRecursive(path)) {
                Class<?> clazz = Class.forName(info.getName());
                if(Module.class.isAssignableFrom(clazz)) {
                    classes.add((Class<? extends Module>) clazz);
                }
            }
        } catch(Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to scan for modules", e);
        }
        registerModules(classes.toArray(new Class[0]));
    }

    public void registerModules(Class<? extends Module>... modules) {
        registerModules(false, modules);
    }

    protected void registerModules(boolean local, Class<? extends Module>... modules) {
        for(Class<? extends Module> moduleClass : modules) {
            try {
                Module instance = (Module) moduleClass.getConstructors()[0].newInstance(plugin);
                instance.local = local;
                moduleMap.put(moduleClass, instance);
            } catch(Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to init module", e);
            }
        }
    }

    protected void enableModules(boolean local) {
        for(Module module : new ArrayList<>(moduleMap.values())) {
            if(module.local != local) continue;
            enableModule(module);
        }

        for(Module module : new ArrayList<>(moduleMap.values())) {
            if(module.local != local) continue;
            enableModule(module);
        }
    }

    protected void disableModules(boolean local) {
        for(Module module : new ArrayList<>(moduleMap.values())) {
            if(module.local != local) continue;
            module.disable();
            module.enabled = false;
        }

        for(Module module : new ArrayList<>(moduleMap.values())) {
            if(module.local != local) continue;
            module.disable();
            module.enabled = false;
        }
    }

    private void enableModule(Module module) {
        if(module.enabled) return;
        List<Class<? extends Module>> required = module.getRequiredModules();
        for(Class<? extends Module> moduleClass : required) {
            Module dep = moduleMap.get(moduleClass);
            enableModule(dep);
        }
        module.enable();
        module.enabled = true;
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        return moduleClass.cast(moduleMap.get(moduleClass));
    }
}