FROM rustfs/rustfs:1.0.0-rc.3

COPY ./docker/rustfs/init-script.sh /opt/scripts/init.sh
RUN chmod +x /opt/scripts/init.sh

ENTRYPOINT ["/opt/scripts/init.sh"]