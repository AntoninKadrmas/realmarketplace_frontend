import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core'
import { NativeScriptCommonModule, NativeScriptRouterModule } from '@nativescript/angular'

import {NewComponent} from "./new.component";
import {ImagePickerComponent} from "./image_picker/image-picker.component";
import {DragAndDropComponent} from "./drag-and-drop/drag-and-drop.component";


@NgModule({
  imports: [
    NativeScriptCommonModule,
    NativeScriptRouterModule.forChild([{
      path: '',
      component: NewComponent,
    }]),
    NativeScriptRouterModule,
  ],
  declarations: [
    NewComponent,
    ImagePickerComponent,
    DragAndDropComponent,
  ],
  schemas: [NO_ERRORS_SCHEMA],
})
export class NewModule {}
