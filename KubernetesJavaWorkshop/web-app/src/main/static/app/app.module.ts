import {NgModule} from "@angular/core";
import {HashLocationStrategy, Location, LocationStrategy} from '@angular/common';
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {HttpModule, JsonpModule } from "@angular/http";
import {AppComponent} from "./app.component";
import {AppRouting} from "./app.routing";
import {ScheduleModule} from 'primeng/primeng';
import {SpeakersComponent} from "./speaker/speakers.component";
import {SpeakerComponent} from "./speaker/speaker.component";
import {SpeakerService} from "./speaker/speaker.service";
import {SessionsComponent} from "./session/sessions.component";
import {SessionComponent} from "./session/session.component";
import {SessionService} from "./session/session.service";
import {SchedulesComponent} from "./schedule/schedules.component";
import {ScheduleComponent} from "./schedule/schedule.component";
import {ScheduleService} from "./schedule/schedule.service";
import {VotesComponent} from "./vote/votes.component";
import {VoteComponent} from "./vote/vote.component";
import {VoteService} from "./vote/vote.service";
import {EndpointsService} from "./shared/endpoints.service";
import {JwtService} from "./shared/jwt.service";
import {RequestService} from "./shared/request.service";
import {SpeakerFilter} from "./speaker/speaker.filter";
import {SessionFilter} from "./session/session.filter";
import {SessionFilterSpeaker} from "./session/session.filter.speaker";
import {SessionSpeakersComponent} from "./session/session.speakers.component";
import {MomentModule} from 'angular2-moment';
import {ChartModule} from 'primeng/primeng';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        JsonpModule,
        ScheduleModule,
        AppRouting,
        MomentModule,
        ChartModule,
    ],
    declarations: [
        AppComponent,
        SpeakersComponent,
        SpeakerComponent,
        SessionsComponent,
        SessionComponent,
        SessionSpeakersComponent,
        SchedulesComponent,
        ScheduleComponent,
        VotesComponent,
        VoteComponent,
        SpeakerFilter,
        SessionFilter,
        SessionFilterSpeaker
    ],
    providers: [
        EndpointsService,
        JwtService,
        RequestService,
        SpeakerService,
        SessionService,
        ScheduleService,
        VoteService,
        Location,
        {provide: LocationStrategy, useClass: HashLocationStrategy}
    ],
    bootstrap: [
        AppComponent
    ]
})

export class AppModule {
}
