# ENRE-Java
ENRE (ENtity Relationship Extractor) is a tool for extraction of code entity dependencies or relationships from source code. 
## Entity Categories
| Entity Type | Description                                           |
| ----------- | ----------------------------------------------------- |
| [Package](entity/Package.md)     | Collect related Files                                 |
| [File](entity/File.md)        | The .java files which save the whole java information |
| [Class](entity/Class.md)       | Like a constructor of objects                         |
| [Enum](entity/Enum.md)        | Like a class only included fixed constants            |
| [EnumConstant](entity/EnumConstant.md)        | A set of predefined constants defined in enum            |
| [Annotation](entity/Annotation.md)  | To get program information while running through it   |
| [AnnotationMember](entity/AnnotationMember.md)| Like a method, which provides extra actions about this annotation|
| [Interface](entity/Interface.md)   | A way to achieve Abstract in Java                     |
| [Method](entity/Method.md)      | To perform specific activity                          |
| [Module](entity/Module.md)      | A closely related set of packages and resources and a new module descriptor file|
| [Record](entity/Record.md)      | A restricted kind of class that defines a simple aggregate of values|
| [TypeParameter](entity/TypeParameter.md)| A container which stores type in generics            |
| [Variable](entity/Variable.md)    | A container which stores values                       |

## Relation Categories

| Dependency type | Description                                                  |
| --------------- | ------------------------------------------------------------ |
| [Import](relation/Import.md)          | A file imports other class, enum or package, or static imports method or var |
| [Inherit](relation/Inherit.md)         | A class inherits the other class                             |
| [Implement](relation/Implement.md)       | A class implement an interface                               |
| [Contain](relation/Contain.md)         | A package contains files, a file contain classes, enums and other types, etc |
| [Call](relation/Call.md)            | A method calls other methods                                 |
| [Parameter](relation/Parameter.md)       | A method needs parameters to use                             |
| [Typed](relation/Typed.md)           | A variable's type is one of the (self-defined) Class or other types                        |
| [UseVar](relation/UseVar.md)          | An entity uses a var in its scope, which could be a local var, a field or a parameter     |
| [Set](relation/Set.md)             | A method set some variables                                  |
| [Modify](relation/Modify.md)          | A method modify variables which have been set                |
| [Annotate](relation/Annotate.md)        | A annotation annotate entities                               |
| [Cast](relation/Cast.md)            | A method cast another type to a variable                     |
| [Override](relation/Override.md)        | A method which has the same name, return type and parameter type of the super method |
| [Reflect](relation/Reflect.md)         | A entity which call Class.forname("...") method to get a specific type. |
| [Define](relation/Define.md)          | A type define fields or methods, a method defines variables. |


## Usage

### 1. Prepare the executable jar

The released jar of ENRE-Java is named as enre_java.jar

### 2. Set up Java environment

To execute enre_java.jar, you should set up Java environment: at least JDK 11 version. 

If the project is quite larger, such as base columns under Android, please increase the memory heap above 8G.

### 3. cmd usage

The usage command is:

```text
java -jar <executable> <lang> <dir> <project-name>
```

The detailed information of the parameter and option of the command is:

```text
Usage: enre_java [-h] [-a=<aidl>] [-hd=<hidden>] [-d=<dir>]... <lang> <src>
                 <projectName>
      <lang>          The lanauge of project files: []
      <src>           The directory to be analyzed
      <projectName>   The analyzed project file name
  -a, --aidl=<aidl>   If the analyzed project is an Android project which
                        contains .aidl files, please provide the corresponding .
                        java files which have the same relative path with the
                        original file
  -d, --dir=<dir>     The additional directories to be analyzed
  -h, --help          display this help and exit
  -hd, --hidden=<hidden> The path of hiddenapi=flag.csv
```

To increase the memory heap, you can add -Xmx before -jar, like: 

```text
java -Xmx20G -jar <executable> <lang> <dir> <include-dir> <project-name>
```

- <executable> The executable jar package of ENRE-Java.
- <lang> The language of source code that will be analysed, now it must be java.
- <dir> The path of the source code that will be analysed.
- <project-name> A short alias name of the analysed source code project.

##### Example:

- Use enre_java.jar to analyse a demo project "halo_1.4.10" written in Java:

```text
# in windows platform
$java -jar enre_java.jar java demo-projects\halo_1.4.10 halo_1.4.10
```

After analysis, ENRE-Java finally outputs the resolved entities and dependencies in JSON files in current directory.

- Use enre_java.jar to analyse the `/base` directory under AOSP, also providing the corresponding `.aidl` path:

```text
# in windows platform
$java -jar enre_java.jar java ...\frameworks\base base -a <aidl-path>
```

- Use enre_java.jar to analyse the `/base` directory under AOSP, also providing several mirror paths which contain source files and share same project name:

```text
# in windows platform
$java -jar enre_java.jar java ...\frameworks\base base -d ...\base
```
