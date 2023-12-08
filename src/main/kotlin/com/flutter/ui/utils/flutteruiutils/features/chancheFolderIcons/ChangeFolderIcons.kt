package com.flutter.ui.utils.flutteruiutils.features.chancheFolderIcons

import com.intellij.ide.IconProvider
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import javax.swing.Icon

enum class MyIcons(
    val path: String,
    val folder: String,
) {
    GearIcon("/icons/ic_gear.svg", "src"),
    FolderDataIcon("/icons/ic_folder_orange_small.svg", "data"),
    FolderDomainIcon("/icons/ic_folder_yellow_small.svg", "domain"),
    FolderPresentationIcon("/icons/ic_folder_green_small.svg", "presentation"),
    FolderUiIcon("/icons/ic_folder_blue_small.svg", "ui"),
    FolderUtilsIcon("/icons/ic_folder_pink_small.svg", "utils"),
    FolderDiIcon("/icons/ic_folder_light_blue_small.svg", "di"),
}

class ChangeFolderIconsProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        return if (element is PsiDirectory) {
            when (element.name) {
                MyIcons.GearIcon.folder -> IconLoader.getIcon(MyIcons.GearIcon.path, javaClass)
                MyIcons.FolderDataIcon.folder -> IconLoader.getIcon(MyIcons.FolderDataIcon.path, javaClass)
                MyIcons.FolderDomainIcon.folder -> IconLoader.getIcon(MyIcons.FolderDomainIcon.path, javaClass)
                MyIcons.FolderPresentationIcon.folder -> IconLoader.getIcon(
                    MyIcons.FolderPresentationIcon.path,
                    javaClass
                )
                MyIcons.FolderUiIcon.folder -> IconLoader.getIcon(MyIcons.FolderUiIcon.path, javaClass)
                MyIcons.FolderUtilsIcon.folder -> IconLoader.getIcon(MyIcons.FolderUtilsIcon.path, javaClass)
                MyIcons.FolderDiIcon.folder -> IconLoader.getIcon(MyIcons.FolderDiIcon.path, javaClass)
                else -> null
            }
        } else {
            null
        }
    }
}

