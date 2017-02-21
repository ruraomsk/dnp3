/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruraomsk.list.ru.cthulhu.pocket;

/**
 *
 * @author Русинов Юрий <ruraomsk@list.ru>
 */
public class SaveFileStatus extends FileStatus {
    
    public SaveFileStatus(Integer number, byte[] file) {
        super(number);
        setSize(file.length);
        setFile(file,0,file.length);
        
    }
    
    @Override
    public String getStatusText() {
        switch (getStatus()) {
            case 0:
                return "Файл готов к передаче";
            case 1:
                return "Передача файла началась";
            case 2:
                return "Файл записывается на устройстве";
            case 3:
                return "Файл передан успешно";
            case -1:
                return "Файл отсутствует на устройстве";
        }
        return "Неизвестный статус";
    }
    
}
