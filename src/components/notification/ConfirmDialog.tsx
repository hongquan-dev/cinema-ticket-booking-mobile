import { Ionicons } from '@expo/vector-icons';
import React from 'react';
import { Modal, Text, TouchableOpacity, View } from 'react-native';

interface ConfirmProps {
    title: string;
    message?: string;
    type?: 'danger' | 'info';
    onClose: (result: boolean) => void;
}

export default function ConfirmDialog({ title, message, onClose, type }: ConfirmProps) {
    const isDanger = type === 'danger';

    return (
        <Modal transparent animationType="fade" visible={true}>
            <View className="flex-1 justify-center items-center bg-black/50 p-6">
                <View className="bg-white w-full max-w-[320px] rounded-2xl shadow-xl overflow-hidden">
                    {/* Header */}
                    <View className="flex-row justify-between items-start p-5">
                        <View className="flex-row items-center flex-1 pr-2">
                            <View className={`p-2 rounded-xl ${isDanger ? 'bg-red-50' : 'bg-blue-50'} mr-3`}>
                                <Ionicons
                                    name={isDanger ? "alert-circle" : "help-circle"}
                                    size={24}
                                    color={isDanger ? "#ef4444" : "#3b82f6"}
                                />
                            </View>
                            <Text className="text-xl font-bold text-gray-800 flex-1">{title}</Text>
                        </View>
                        <TouchableOpacity onPress={() => onClose(false)}>
                            <Ionicons name="close" size={24} color="#9ca3af" />
                        </TouchableOpacity>
                    </View>

                    {/* Body */}
                    {message && (
                        <View className="px-5 pb-5">
                            <Text className="text-gray-500 text-md leading-5">{message}</Text>
                        </View>
                    )}

                    {/* Footer */}
                    <View className="flex-row justify-end p-4 bg-gray-50/50 border-t border-gray-100">
                        <TouchableOpacity
                            onPress={() => onClose(false)}
                            className="px-5 py-2.5 rounded-xl border border-gray-200 bg-white mr-2"
                        >
                            <Text className="text-gray-700 font-semibold">Thoát</Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            onPress={() => onClose(true)}
                            className={`px-7 py-2.5 rounded-xl shadow-sm ${isDanger ? 'bg-red-600' : 'bg-[#3bacef]'}`}
                        >
                            <Text className="text-white font-bold">Có</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </Modal>
    );
}