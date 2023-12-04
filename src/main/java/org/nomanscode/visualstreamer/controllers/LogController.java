package org.nomanscode.visualstreamer.controllers;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.database.LogRepository;
import org.nomanscode.visualstreamer.exceptions.SSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.Disposable;
import reactor.core.publisher.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class LogController {

    @Autowired
    private LogRepository logRepository;

    private CybertronEmitterProcessor<LogEnvelope> emitter = CybertronEmitterProcessor.create(false);
    private CybertronEmitterProcessor<Info<UUID, LogEnvelope>> outputEmitter = CybertronEmitterProcessor.create(false);

    private Disposable subscription;

    public LogController() {

    }


    @PostConstruct
    private void postConstruct() {
        try {
            this.subscription = this.emitter.getProcessor().onBackpressureBuffer().subscribe(e -> {

                try {
                    switch (e.level) {
                        case WARNING:
                            log.warn(e.title);
                            this.logRepository.writeWarning(e.title, e.nodeName);
                            break;
                        case COMMON_ERROR:
                            log.error(e.title);
                            this.logRepository.writeError(e.title, e.nodeName);
                            break;
                        case SEVERE_ERROR:
                            log.error("SEVERE: " + e.title, e.cause);
                            this.logRepository.writeSevereError(e.title, e.cause, e.nodeName);
                            break;
                        default:
                            log.info(e.title);
                            this.logRepository.writeInformation(e.title, e.nodeName);
                            break;
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    if ( this.subscription  != null ) {
                        this.subscription.dispose();
                        this.subscription = null;
                    }
                }

            });

            if (this.subscription == null) {
                log.error("Unable to activate the Log Controller properly");
            }
            else {
                this.info("Current Node", "");
            }
        }
        catch(Exception e) {
            log.error("Unable to activate the Log Controller properly. Reason: " + e.getMessage());
            if ( this.subscription != null ) {
                this.subscription.dispose();
                this.subscription = null;
            }
        }
    }

    @PreDestroy
    private void preDestroy() {
        if ( this.subscription != null) {
            this.subscription.dispose();
            this.subscription = null;
        }
    }

    public LogRepository getRepository() {
        return this.logRepository;
    }

    public void info(final String nodeName, final String title) {

        LogEnvelope envelope = new LogEnvelope(nodeName, title, ErrorLevel.INFORMATION);
        this.emitter.onNext(envelope);
        this.outputEmitter.onNext( Info.create(envelope.id, envelope));
    }

    public void warn(final String nodeName,final String title) {
        LogEnvelope envelope = new LogEnvelope(nodeName, title,  ErrorLevel.WARNING);
        this.emitter.onNext(envelope);
        this.outputEmitter.onNext( Info.create(envelope.id, envelope));
    }

    public void error(final String nodeName, final String title) {
        LogEnvelope envelope = new LogEnvelope(nodeName, title,  ErrorLevel.COMMON_ERROR);
        this.emitter.onNext(envelope);
        this.outputEmitter.onNext( Info.create(envelope.id, envelope));
    }

    public void severe(final String nodeName, final String title, Throwable t) {
        severe(nodeName, title, t.getCause().toString());
    }

    public void severe(final String nodeName, final String title, final String cause) {
        LogEnvelope envelope = new LogEnvelope(nodeName, title, cause, ErrorLevel.SEVERE_ERROR);
        this.emitter.onNext(envelope);
        this.outputEmitter.onNext( Info.create(envelope.id, envelope));
    }

    public Flux<Info<UUID, LogEnvelope>> getLogFlux(LogRequest request) {
        try {
            return Flux.defer( () -> this.getLogFromRepository(request))
                    .concatWith(this.outputEmitter.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("LogSearch: Complete"))
                    .doOnCancel(() -> log.warn("LogSearch: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    public Flux<Info<UUID, LogEnvelope>> getLogFromRepository(LogRequest request)
    {
        try {
            List<LogEnvelope> logs = this.logRepository.getLogs(request.getStartDate(),
                                                                request.getEndDate(),
                                                                request.getSubject(),
                                                                request.getLevels(),
                                                                request.getNodeName());
            if ( logs == null ) {
                return Flux.empty();
            }

            return Flux.fromIterable(logs.stream()
                                         .map( envelope -> Info.create(envelope.id, envelope))
                                         .collect(Collectors.toList()));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }

    }

}

