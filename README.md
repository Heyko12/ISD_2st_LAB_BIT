# Проверка работы и запуск

Запустить контейнеры можно командой "sh init.sh". Убедиться в корректной работе симулированных выделенных нами машин можно командой "docker exec -t -i mysql /bin/bash
", набранной после исполнения скрипта init.sh. При исполнении этой команды мы попадаем в контейнер нашей симулированной базы данных - mysql, имитируя подключение к ней по ssh. Подключившись в БД, вызываем "mysql", так мы попадаем в терминальное приложение нашей БД, в котором должны позвать "connect mydatabase", чтобы подключиться к созданной нами в рамках деплоя ансиблом базе данных. Наконец, чтобы посмотреть заполнение таблицы в этой БД данными, которое генерирует написанный нами Producer на хосте producer, следует позвать "select * from mytable;".

# Описание

В репозитории лежат папки Consumer и Producer - в них .java файлы для приложения, которое генерирует всякий мусор и для приложения, которое его потребляет и отдает в БД. Содержимое этих папок собирается на чистом эксекьютере - внутри контейнера builder - и кладется в папку app.

После, в докер-компоузе поднимаются 4 контейнера - консьюмера, продьюсера, БД и контейнер ансибловского хоста, каждый из них симулирует отдельно выделенный сервер. Предполагается в рамках симуляции, что контейнеры консьюмера, продьюсера и БД изначально пусты, ничего из себя не представляют, на них установлены только питон (для взаимодействия с ансиблом) и ssh, по которым к ним можно подключиться. Контейнер ансибла симулирует машину, на которой уже предустановлен сам ансибл.

После начинает свою работу ансибловский плейбук, который оперирует ролями application_consumer, aplication_producer и my_sql. Рассмотрим их все подробнее:

- application_consumer: В рамках этой роли мы устанавливаем на машину джаву, деплоим в нее файл для запуска, разархивируем этот файл и запускаем в фоне (джарник коннектора лежит рядом с приложением и поставляется джарником Consumer.jar, не деплоится отдельно). Приложение, которое мы запустили преследует простую цель: слушает свой выделенный порт, принимает по нему сокет и обрабатывает, вытаскивая из него структуру MiniClass, которую позже отправляет в БД.

- application_producer: В рамках этой роли мы поступаем аналогично роли application_consumer, разве что, названия деплоимых джарников отличаются и джарник коннектора подставлять в classpath не надо. Приложение работает так: генерит каждые пять секунд случайные строки (имя какого то пользователя и его якобы пароль) и отправляет консьюмеру сгенерированную структуру MiniClass.

- my_sql: В рамках этой роли мы сначала накатываем на машину питон, потом марию и запускаем ее. Далее деплоим на машину файл конфигурации нашей БД, создаем БД на его основании, в созданной БД создаем таблицу и пользователя. Пользователь, название создаваемой в рамках деплоя БД и таблицы не захардкожены, а вынесены в папку defaults внутри роли.

Самое сложное - виртуализировать внутри докера выделенные сервера, из-под каждого из которых видно все прочие. Чтобы все работало и симуляции серверов могли общаться друг с другом надо правильно настроить сервера и хост-машину ансибл для ssh-подключений (первые раны в соответствующих докерфайлах), правильно указать их ip в докер-компоузе в рамках создаваемой внутри него сети, а также не забыть, что те же хосты следует указать в hosts в inventories, чтобы ансибл тоже знал как к ним обращаться.