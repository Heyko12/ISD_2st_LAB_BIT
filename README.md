* Проверка работы и запуск

Запустить контейнеры можно командой "sh init.sh". Убедиться в корректной работе симулированных выделенных нами машин можно командой "docker exec -t -i mysql /bin/bash
", набранной после исполнения скрипта init.sh. При исполнении этой команды мы попадаем в контейнер нашей симулированной базы данных - mysql, имитируя подключение к ней по ssh. Подключившись в БД, вызываем "mysql", так мы попадаем в терминальное приложение нашей БД, в котором должны позвать "connect mydatabase", чтобы подключиться к созданной нами в рамках деплоя ансиблом базе данных. Наконец, чтобы посмотреть заполнение таблицы в этой БД данными, которое генерирует написанный нами Producer на хосте producer, следует позвать "select * from mytable;".

* Описание

В репозитории лежат папки Consumer и Producer - в них .java файлы для приложения, которое генерирует всякий мусор и для приложения, которое его потребляет и отдает в БД. Содержимое этих папок собирается на чистом эксекьютере - внутри контейнера builder - и кладется в папку app.

После, в докер-компоузе поднимаются 4 контейнера - консьюмера, продьюсера, БД и контейнер ансибловского хоста, каждый из них симулирует отдельно выделенный сервер. Предполагается в рамках симуляции, что контейнеры консьюмера, продьюсера и БД изначально пусты, ничего из себя не представляют, на них установлены только ssh, по которым к ним можно подключиться. Контейнер ансибла симулирует машину, на которой уже предустановлен сам ансибл.

После начинает свою работу ансибловский плейбук, который оперирует ролями application_consumer, pplication_producer и my_sql. Рассмотрим их все подробнее:

- application_consumer: В рамках этой роли мы устанавливаем на машину джаву, деплоим в нее файл для запуска, разархивируем этот файл и запускаем в фоне